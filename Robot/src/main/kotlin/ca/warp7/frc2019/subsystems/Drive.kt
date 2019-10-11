package ca.warp7.frc2019.subsystems

import ca.warp7.frc.control.Delta
import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.drive.WheelState
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.*
import ca.warp7.frc.linearInterpolate
import ca.warp7.frc.path.parameterized
import ca.warp7.frc.path.quinticSplinesOf
import ca.warp7.frc.squared
import ca.warp7.frc.trajectory.TrajectoryState
import ca.warp7.frc.trajectory.generateTrajectory
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.followers.PosePIDFollower
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import com.ctre.phoenix.motorcontrol.ControlMode
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import kotlin.math.*

object Drive {

    // Drive Train Hardware Constants

    const val kTicksPerRadian = 1024 / (2 * PI) // ticks/rad

    private const val kWheelBaseRadius = 0.44 // metres
    private const val kWheelRadius = 3 * 0.0254 // m
    private const val kMaxFreeSpeed = 5.0 // m/s

    private const val kTicksPerMeterPer100ms = kTicksPerRadian / kWheelRadius * 0.1 // ticks/m*100ms

    // Dynamic Constants

    private const val kMaxVoltage = 12.0 // V
    private const val kFrictionVoltage = 1.0 // V
    private const val kMass = 60.0 // kg
    private const val kMomentOfInertia = 10.0 // kg * m^2
    private const val kAngularDrag = 0.0 // (N * m) / (rad/s)

    private const val kSpeedPerVolt = kMaxFreeSpeed / kMaxVoltage // (m/s) / V

    private const val kTorquePerVolt =
            0.5 * kWheelRadius * kWheelRadius * kMass / 0.012  // (N * m) / V

    private const val kEpsilon = 1E-9

    private val io: BaseIO = ioInstance()

    fun neutralOutput() {
        io.driveControlMode = ControlMode.Disabled
        io.leftDemand = 0.0
        io.rightDemand = 0.0
        io.leftFeedforward = 0.0
        io.rightFeedforward = 0.0
    }

    fun setVoltageOnly() {
        io.driveControlMode = ControlMode.PercentOutput
        io.leftDemand = 0.0
        io.rightDemand = 0.0
    }

    fun setWheelDynamics(velocity: WheelState, voltage: WheelState) {
        io.driveControlMode = ControlMode.Velocity
        io.leftDemand = velocity.left * kTicksPerMeterPer100ms
        io.rightDemand = velocity.right * kTicksPerMeterPer100ms
        io.leftFeedforward = voltage.left / kMaxVoltage
        io.rightFeedforward = voltage.right / kMaxVoltage
    }

    private fun getVoltage(linearVelocity: Double, wheelSpeed: Double, wheelTorque: Double): Double {
        val direction = when {
            linearVelocity > kEpsilon -> 1.0 // linear
            linearVelocity < -kEpsilon -> -1.0 // linear
            wheelSpeed > kEpsilon -> 1.0 // angular
            wheelSpeed < -kEpsilon -> -1.0 // angular
            wheelTorque > kEpsilon -> 1.0 // acceleration
            wheelTorque < -kEpsilon -> -1.0 // acceleration
            else -> 0.0
        }
        return wheelSpeed / kSpeedPerVolt + wheelTorque / kTorquePerVolt + direction * kFrictionVoltage
    }

    fun setChassisDynamics(velocity: ChassisState, acceleration: ChassisState) {
        val leftSpeed = velocity.linear - velocity.angular * kWheelBaseRadius
        val leftTorque = 0.5 * kWheelRadius * (acceleration.linear * kMass -
                acceleration.angular * kMomentOfInertia / kWheelBaseRadius -
                velocity.angular * kAngularDrag / kWheelBaseRadius)
        val leftVoltage = getVoltage(velocity.linear, leftSpeed, leftTorque)

        val rightSpeed = velocity.linear + velocity.angular * kWheelBaseRadius
        val rightTorque = 0.5 * kWheelRadius * (acceleration.linear * kMass +
                acceleration.angular * kMomentOfInertia / kWheelBaseRadius +
                velocity.angular * kAngularDrag / kWheelBaseRadius)
        val rightVoltage = getVoltage(velocity.linear, rightSpeed, rightTorque)

        setWheelDynamics(WheelState(leftSpeed, rightSpeed), WheelState(leftVoltage, rightVoltage))
    }

    private var previousVelocity = ChassisState(0.0, 0.0)

    fun setAdjustedVelocity(newLinear: Double, newAngular: Double) {
        val newVelocity = ChassisState(newLinear, newAngular)
        previousVelocity = newVelocity
        val linearAcc = (newLinear - previousVelocity.linear) / io.dt
        val angularAcc = (newAngular - previousVelocity.angular) / io.dt
        val newAcceleration = ChassisState(linearAcc, angularAcc)
        setChassisDynamics(newVelocity, newAcceleration)
    }

    fun setAdjustedCurvature(velocity: ChassisState, curvature: Double, xError: Double) {
        val adjustedLinear: Double
        val adjustedAngular: Double
        if (curvature.isInfinite()) {
            adjustedLinear = 0.0
            adjustedAngular = velocity.angular
        } else {
            adjustedLinear = velocity.linear + PosePIDFollower.kX * xError
            adjustedAngular = curvature * velocity.linear
        }
        setAdjustedVelocity(adjustedLinear, adjustedAngular)
    }

    var robotState: Pose2D = Pose2D.identity
    var chassisVelocity = ChassisState(0.0, 0.0)

    private val dLeft = Delta()
    private val dRight = Delta()

    fun updateRobotStateEstimation() {
        val left = io.leftVelocity * kWheelRadius
        val right = io.rightVelocity * kWheelRadius
        chassisVelocity = ChassisState(
                (left + right) / 2.0,
                (right - left) / (2.0 * kWheelBaseRadius)
        )
        val dTheta =
                if (io.gyroConnected) io.yaw - io.previousYaw
                else Rotation2D.fromRadians(io.dt * chassisVelocity.angular)
        val theta = robotState.rotation + dTheta
        val arcLength = (dLeft.update(io.leftPosition) + dRight.update(io.rightPosition)) * kWheelRadius / 2
        val dThetaRad = dTheta.radians
        val chordLength =
                if (dThetaRad.epsilonEquals(0.0, 0.01)) arcLength
                else sin(dThetaRad / 2) * arcLength / dThetaRad * 2
        val pos = robotState.translation + theta.translation * chordLength
        robotState = Pose2D(pos, theta)
    }

    private var quickStopAccumulator = 0.0 // gain for stopping from quick turn
    private const val kQuickStopAlpha = 0.1
    fun updateCurvatureDrive(xSpeed: Double, zRotation: Double, isQuickTurn: Boolean) {
        val angularPower: Double

        if (isQuickTurn) {
            if (abs(xSpeed) < 0.2) {
                quickStopAccumulator = (1 - kQuickStopAlpha) * quickStopAccumulator + kQuickStopAlpha * zRotation * 2.0
            }
            angularPower = (zRotation * zRotation + kFrictionVoltage / kMaxVoltage)
                    .withSign(zRotation).coerceIn(-0.8, 0.8)
        } else {
            angularPower = abs(xSpeed) * zRotation - quickStopAccumulator
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
        val maxMagnitude = max(abs(leftMotorOutput), abs(rightMotorOutput))
        if (maxMagnitude > 1.0) {
            leftMotorOutput /= maxMagnitude
            rightMotorOutput /= maxMagnitude
        }

        io.driveControlMode = ControlMode.PercentOutput
        io.leftDemand = leftMotorOutput
        io.rightDemand = rightMotorOutput
    }


    private var t = 0.0
    private var trajectory: List<TrajectoryState> = listOf()
    private var totalTime = 0.0
    private var direction = 0.0
    private var initialState: Pose2D = Pose2D.identity
    private var trajectoryGenerator: Future<List<TrajectoryState>>? = null

    fun initTrajectory(waypoints: Array<Pose2D>, maxVelocity: Double, maxAcceleration: Double,
                       maxCentripetalAcceleration: Double, backwards: Boolean, absolute: Boolean,
                       enableJerkLimiting: Boolean, optimizeDkSquared: Boolean) {
        direction = if (backwards) -1.0 else 1.0
        val maxJerk = if (enableJerkLimiting) DriveConstants.kMaxJerk else Double.POSITIVE_INFINITY

        trajectoryGenerator = FutureTask {
            val path =
                    if (absolute) quinticSplinesOf(robotState, *waypoints, optimizePath = optimizeDkSquared)
                    else quinticSplinesOf(*waypoints, optimizePath = optimizeDkSquared)
            val parameterizedPath = path.parameterized()
            generateTrajectory(parameterizedPath, kWheelBaseRadius,
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

    fun interpolatedTimeView(t: Double): TrajectoryState {
        var index = 0
        while (index < trajectory.size - 2 && trajectory[index + 1].t < t) index++
        val last = trajectory[index]
        val next = trajectory[index + 1]
        val interpolant = if (last.t.epsilonEquals(next.t)) 1.0 else (t - last.t) / (next.t - last.t)

        val v = direction * linearInterpolate(last.v, next.v, interpolant)
        val dv = direction * linearInterpolate(last.dv, next.dv, interpolant)
        val w = linearInterpolate(last.w, next.w, interpolant)
        val dw = linearInterpolate(last.dw, next.dw, interpolant)

        val curvature = linearInterpolate(last.arcPose.curvature, next.arcPose.curvature, interpolant)
        val position = last.arcPose.translation.interpolate(next.arcPose.translation, interpolant)
        val heading = last.arcPose.rotation.interpolate(next.arcPose.rotation, interpolant)
        val pose = ArcPose2D(Pose2D(position, heading), curvature, 0.0)

        return TrajectoryState(pose, v, w, dv, dw, 0.0, 0.0, t)
    }

    fun advanceTrajectory(dt: Double): TrajectoryState {
        t += dt
        return interpolatedTimeView(t)
    }

    fun isDoneTrajectory(): Boolean {
        return t > totalTime
    }

    fun updatePosePID(error: Pose2D, velocity: ChassisState) {
        val adjustedLinear = velocity.linear +
                DriveConstants.kPoseX * error.translation.x
        val adjustedAngular = velocity.angular +
                velocity.linear * DriveConstants.kPoseY * error.translation.y +
                DriveConstants.kPoseTheta * error.rotation.radians
        setAdjustedVelocity(adjustedLinear, adjustedAngular)
    }

    fun updateAnglePID(velocity: ChassisState) {
        val error = velocity.angular - io.angularVelocity
        val adjustedAngular = velocity.angular + velocity.linear * DriveConstants.kAngleP * error
        setAdjustedVelocity(velocity.linear, adjustedAngular)
    }

    fun getLookahead(setpoint: TrajectoryState): TrajectoryState {
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


    fun updatePurePursuit(error: Pose2D, setpoint: TrajectoryState) {
        val lookahead = getLookahead(setpoint)
        val initialToRobot = getInitialToRobot()
        val velocity = setpoint.velocity
        val curvature = 1.0 / findRadius(initialToRobot, lookahead.arcPose)
        setAdjustedCurvature(velocity, curvature, error.translation.x)
    }

    fun updateSimplePurePursuit(error: Pose2D, setpoint: TrajectoryState) {
        val lookahead = getLookahead(setpoint)
        val initialToRobot = getInitialToRobot()
        val velocity = setpoint.velocity
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