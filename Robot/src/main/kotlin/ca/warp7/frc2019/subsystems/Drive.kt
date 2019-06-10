package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Delta
import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.drive.DynamicState
import ca.warp7.frc.drive.WheelState
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.*
import ca.warp7.frc.linearInterpolate
import ca.warp7.frc.path.parameterized
import ca.warp7.frc.path.quinticSplinesOf
import ca.warp7.frc.squared
import ca.warp7.frc.trajectory.TrajectoryPoint
import ca.warp7.frc.trajectory.findRadius
import ca.warp7.frc.trajectory.timedTrajectory
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import com.ctre.phoenix.motorcontrol.ControlMode
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.withSign

object Drive {
    private val io: BaseIO = ioInstance()
    val model = DifferentialDriveModel(
            DriveConstants.kWheelRadius, DriveConstants.kEffectiveWheelBaseRadius, DriveConstants.kMaxVelocity,
            DriveConstants.kMaxAcceleration, DriveConstants.kMaxFreeSpeed, DriveConstants.kSpeedPerVolt,
            DriveConstants.kTorquePerVolt, DriveConstants.kFrictionVoltage, DriveConstants.kLinearInertia,
            DriveConstants.kAngularInertia, DriveConstants.kMaxVolts, DriveConstants.kAngularDrag
    )

    fun neutralOutput() {
        io.driveControlMode = ControlMode.Disabled
        io.leftDemand = 0.0
        io.rightDemand = 0.0
        io.leftFeedforward = 0.0
        io.rightFeedforward = 0.0
    }

    fun setVoltage(dynamicState: DynamicState) {
        io.driveControlMode = ControlMode.PercentOutput
        io.leftDemand = 0.0
        io.rightDemand = 0.0
        io.leftFeedforward = dynamicState.voltage.left / model.maxVoltage
        io.rightFeedforward = dynamicState.voltage.right / model.maxVoltage
    }

    fun setVoltage(velocity: ChassisState, acceleration: ChassisState) {
        setVoltage(model.solve(velocity, acceleration))
    }

    fun setDynamics(dynamicState: DynamicState) {
        io.driveControlMode = ControlMode.Velocity
        io.leftDemand = dynamicState.velocity.left * DriveConstants.kTicksPerMeterPer100ms
        io.rightDemand = dynamicState.velocity.right * DriveConstants.kTicksPerMeterPer100ms
        io.leftFeedforward = dynamicState.voltage.left / model.maxVoltage
        io.rightFeedforward = dynamicState.voltage.right / model.maxVoltage
    }

    fun setDynamics(velocity: ChassisState, acceleration: ChassisState) {
        setDynamics(model.solve(velocity, acceleration))
    }

    fun setAdjustedDynamics(dynamics: DynamicState, adjustedLinear: Double, adjustedAngular: Double) {
        val (adjustedLeft, adjustedRight) = model.solve(ChassisState(adjustedLinear, adjustedAngular))
        val leftVoltage = dynamics.voltage.left + (adjustedLeft - dynamics.velocity.left) / model.speedPerVolt
        val rightVoltage = dynamics.voltage.right + (adjustedRight - dynamics.velocity.right) / model.speedPerVolt
        setDynamics(DynamicState(WheelState(leftVoltage, rightVoltage), dynamics.velocity))
    }

    private var previousVelocity = ChassisState(0.0, 0.0)
    fun setAdjustedVelocity(adjustedLinear: Double, adjustedAngular: Double) {
        val adjustedVelocity = ChassisState(adjustedLinear, adjustedAngular)
        previousVelocity = adjustedVelocity
        val linearAcc = (adjustedLinear - previousVelocity.linear) / io.dt
        val angularAcc = (adjustedAngular - previousVelocity.angular) / io.dt
        val adjustedAcceleration = ChassisState(linearAcc, angularAcc)
        setDynamics(adjustedVelocity, adjustedAcceleration)
    }

    private var quickStopAccumulator = 0.0 // gain for stopping from quick turn
    private const val kQuickStopAlpha = 0.1
    fun updateCurvatureDrive(xSpeed: Double, zRotation: Double, isQuickTurn: Boolean) {
        val angularPower: Double

        if (isQuickTurn) {
            if (Math.abs(xSpeed) < 0.2) {
                quickStopAccumulator = (1 - kQuickStopAlpha) * quickStopAccumulator + kQuickStopAlpha * zRotation * 2.0
            }
            angularPower = (zRotation * zRotation + model.frictionPercent).withSign(zRotation).coerceIn(-0.8, 0.8)
        } else {
            angularPower = Math.abs(xSpeed) * zRotation - quickStopAccumulator
            when {
                quickStopAccumulator > 1 -> quickStopAccumulator -= 1.0
                quickStopAccumulator < -1 -> quickStopAccumulator += 1.0
                else -> quickStopAccumulator = 0.0
            }
        }

        var leftMotorOutput = xSpeed + angularPower
        var rightMotorOutput = xSpeed - angularPower

        // If rotation is overpowered, reduce both outputs to within acceptable range
        if (isQuickTurn) when {
            leftMotorOutput > 1.0 -> {
                rightMotorOutput -= leftMotorOutput - 1.0
                leftMotorOutput = 1.0
            }
            rightMotorOutput > 1.0 -> {
                leftMotorOutput -= rightMotorOutput - 1.0
                rightMotorOutput = 1.0
            }
            leftMotorOutput < -1.0 -> {
                rightMotorOutput -= leftMotorOutput + 1.0
                leftMotorOutput = -1.0
            }
            rightMotorOutput < -1.0 -> {
                leftMotorOutput -= rightMotorOutput + 1.0
                rightMotorOutput = -1.0
            }
        }

        // Normalize the wheel speeds
        val maxMagnitude = Math.max(Math.abs(leftMotorOutput), Math.abs(rightMotorOutput))
        if (maxMagnitude > 1.0) {
            leftMotorOutput /= maxMagnitude
            rightMotorOutput /= maxMagnitude
        }

        io.driveControlMode = ControlMode.PercentOutput
        io.leftDemand = leftMotorOutput
        io.rightDemand = rightMotorOutput
    }

    var robotState: Pose2D = Pose2D.identity
    var chassisVelocity = ChassisState(0.0, 0.0)

    private val dLeft = Delta()
    private val dRight = Delta()

    fun updateRobotStateEstimation() {
        val wheels = WheelState(io.leftVelocity * model.wheelRadius, io.rightVelocity * model.wheelRadius)
        chassisVelocity = model.solve(wheels)
        val dTheta =
                if (io.gyroConnected) io.yaw - io.previousYaw
                else Rotation2D.fromRadians(io.dt * chassisVelocity.angular)
        val theta = robotState.rotation + dTheta
        val arcLength = (dLeft.update(io.leftPosition) + dRight.update(io.rightPosition)) * model.wheelRadius / 2
        val dThetaRad = dTheta.radians
        val chordLength =
                if (dThetaRad.epsilonEquals(0.0, 0.01)) arcLength
                else sin(dThetaRad / 2) * arcLength / dThetaRad * 2
        val pos = robotState.translation + theta.translation * chordLength
        robotState = Pose2D(pos, theta)
    }

    private var t = 0.0
    private var trajectory: List<TrajectoryPoint> = listOf()
    private var totalTime = 0.0
    private var direction = 0.0
    private var initialState: Pose2D = Pose2D.identity
    private var trajectoryGenerator: Future<List<TrajectoryPoint>>? = null

    fun initTrajectory(waypoints: Array<Pose2D>, maxVelocity: Double, maxAcceleration: Double,
                       maxCentripetalAcceleration: Double, backwards: Boolean, absolute: Boolean,
                       enableJerkLimiting: Boolean, optimizeDkSquared: Boolean) {
        direction = if (backwards) -1.0 else 1.0
        val maxJerk = if (enableJerkLimiting) DriveConstants.kMaxJerk else Double.POSITIVE_INFINITY
        trajectoryGenerator = FutureTask {
            // create a future task because it takes too long for each loop
            val path =
                    if (absolute) quinticSplinesOf(robotState, *waypoints, optimizePath = optimizeDkSquared)
                    else quinticSplinesOf(*waypoints, optimizePath = optimizeDkSquared)
            val pathStates = path.parameterized()
            pathStates.timedTrajectory(model.wheelbaseRadius, 0.0, 0.0,
                    maxVelocity, maxAcceleration, maxCentripetalAcceleration, maxJerk)
        }.also { Thread(it).start() }
        previousVelocity = ChassisState(0.0, 0.0)
        neutralOutput()
        io.drivePID = DriveConstants.kTrajectoryPID
    }

    fun tryFinishGeneratingTrajectory(): Boolean {
        val generator = trajectoryGenerator
        if (generator == null || !generator.isDone) return false // Check if generator is done generating
        trajectory = generator.get()
        totalTime = trajectory.last().t // reset tracking state
        t = 0.0
        val firstState = trajectory.first().arcPose
        initialState = Pose2D((robotState.translation - firstState.translation).rotate(-firstState.rotation),
                robotState.rotation - firstState.rotation)
        return true
    }

    fun getInitialToRobot(): Pose2D {
        return Pose2D((robotState.translation - initialState.translation)
                .rotate(-initialState.rotation), robotState.rotation - initialState.rotation)
    }

    fun getError(setpoint: ArcPose2D): Pose2D {
        val initialToRobot = getInitialToRobot()
        return Pose2D((setpoint.translation - initialToRobot.translation)
                .rotate(-initialToRobot.rotation), setpoint.rotation - initialToRobot.rotation)
    }

    fun interpolatedTimeView(t: Double): TrajectoryPoint {
        var index = 0
        while (index < trajectory.size - 2 && trajectory[index + 1].t < t) index++
        val last = trajectory[index]
        val next = trajectory[index + 1]
        val interpolant = if (last.t.epsilonEquals(next.t)) 1.0 else (t - last.t) / (next.t - last.t)
        val acceleration = direction * linearInterpolate(last.acceleration, next.acceleration, interpolant)
        val velocity = direction * linearInterpolate(last.velocity, next.velocity, interpolant)
        val curvature = linearInterpolate(last.arcPose.curvature, next.arcPose.curvature, interpolant)
        val position = last.arcPose.translation.interpolate(next.arcPose.translation, interpolant)
        val heading = last.arcPose.rotation.interpolate(next.arcPose.rotation, interpolant)
        val arcPose = ArcPose2D(Pose2D(position, heading), curvature, 0.0)
        return TrajectoryPoint(arcPose, velocity, acceleration, 0.0, t)
    }

    fun advanceTrajectory(dt: Double): TrajectoryPoint {
        t += dt
        return interpolatedTimeView(t)
    }

    fun isDoneTrajectory(): Boolean {
        return t > totalTime
    }

    fun updatePosePID(error: Pose2D, velocity: ChassisState, acceleration: ChassisState) {
        val adjustedLinear = velocity.linear +
                DriveConstants.kPoseX * error.translation.x
        val adjustedAngular = velocity.angular +
                velocity.linear * DriveConstants.kPoseY * error.translation.y +
                DriveConstants.kPoseTheta * error.rotation.radians
        setAdjustedDynamics(model.solve(velocity, acceleration), adjustedLinear, adjustedAngular)
    }

    fun updateAnglePID(velocity: ChassisState, acceleration: ChassisState) {
        val error = velocity.angular - io.angularVelocity
        val adjustedAngular = velocity.angular + velocity.linear * DriveConstants.kAngleP * error
        setAdjustedDynamics(model.solve(velocity, acceleration), velocity.linear, adjustedAngular)
    }

    fun getLookahead(setpoint: TrajectoryPoint): TrajectoryPoint {
        var lookaheadTime = DriveConstants.kPathLookaheadTime
        var lookahead = interpolatedTimeView(lookaheadTime)
        var lookaheadDistance = setpoint.arcPose.distanceTo(lookahead.arcPose)
        while (lookaheadDistance < DriveConstants.kMinLookDist && (totalTime - t) > lookaheadTime) {
            lookaheadTime += DriveConstants.kLookaheadSearchDt
            lookahead = interpolatedTimeView(lookaheadTime)
            lookaheadDistance = setpoint.arcPose.distanceTo(lookahead.arcPose)
        }
        if (lookaheadDistance < DriveConstants.kMinLookDist) lookahead = trajectory.last()
        return lookahead
    }

    fun setAdjustedCurvature(velocity: ChassisState, curvature: Double, xError: Double) {
        val adjustedLinear: Double
        val adjustedAngular: Double
        if (curvature.isInfinite()) {
            adjustedLinear = 0.0
            adjustedAngular = velocity.angular
        } else {
            adjustedLinear = velocity.linear + DriveConstants.kPoseX * xError
            adjustedAngular = curvature * velocity.linear
        }
        setAdjustedVelocity(adjustedLinear, adjustedAngular)
    }

    fun updatePurePursuit(error: Pose2D, setpoint: TrajectoryPoint) {
        val lookahead = getLookahead(setpoint)
        val initialToRobot = getInitialToRobot()
        val velocity = setpoint.chassisVelocity
        val curvature = 1.0 / findRadius(initialToRobot, lookahead.arcPose)
        setAdjustedCurvature(velocity, curvature, error.translation.x)
    }

    fun updateSimplePurePursuit(error: Pose2D, setpoint: TrajectoryPoint) {
        val lookahead = getLookahead(setpoint)
        val initialToRobot = getInitialToRobot()
        val velocity = setpoint.chassisVelocity
        val y = (lookahead.arcPose.translation - initialToRobot.translation).rotate(-initialToRobot.rotation).y
        val l = initialToRobot.distanceTo(lookahead.arcPose.pose)
        val curvature = (2 * y) / l.squared
        setAdjustedCurvature(velocity, curvature, error.translation.x)
    }

    // Equation 5.12 from https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
    fun updateRamsete(error: Pose2D, velocity: ChassisState) {
        val k = 2.0 * DriveConstants.kRamseteZeta * sqrt(
                DriveConstants.kRamseteBeta * velocity.linear.squared + velocity.angular.squared) // gain term
        val turnError = error.rotation.radians
        val sinRatio = if (turnError.epsilonEquals(0.0, 1E-2)) 1.0 else error.rotation.sin / turnError
        val adjustedLinear = velocity.linear * error.rotation.cos + // current linear velocity
                k * error.translation.x // forward error correction
        val adjustedAngular = velocity.angular + // current angular velocity
                k * turnError + // turn error correction
                velocity.linear * DriveConstants.kRamseteBeta * sinRatio * error.translation.y // lateral correction
        setAdjustedVelocity(adjustedLinear, adjustedAngular)
    }
}