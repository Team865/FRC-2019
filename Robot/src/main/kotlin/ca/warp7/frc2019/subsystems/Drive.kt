package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Delta
import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.drive.DynamicState
import ca.warp7.frc.drive.WheelState
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.*
import ca.warp7.frc.interpolate
import ca.warp7.frc.path.parameterized
import ca.warp7.frc.path.quinticSplinesOf
import ca.warp7.frc.trajectory.TrajectoryPoint
import ca.warp7.frc.trajectory.timedTrajectory
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.withSign

object Drive {

    private val io: BaseIO = ioInstance()

    val model = DifferentialDriveModel(
            DriveConstants.kWheelRadius, DriveConstants.kEffectiveWheelBaseRadius,
            DriveConstants.kMaxVelocity, DriveConstants.kMaxAcceleration, DriveConstants.kMaxFreeSpeed,
            DriveConstants.kSpeedPerVolt, DriveConstants.kTorquePerVolt, DriveConstants.kFrictionVoltage,
            DriveConstants.kLinearInertia, DriveConstants.kAngularInertia,
            DriveConstants.kMaxVolts, DriveConstants.kAngularDrag
    )

    fun neutralOutput() {
        io.driveControlMode = ControlMode.Disabled
        io.leftDemand = 0.0
        io.rightDemand = 0.0
        io.leftFeedforward = 0.0
        io.rightFeedforward = 0.0
    }

    fun setPercent(left: Double, right: Double) {
        io.driveControlMode = ControlMode.PercentOutput
        io.leftDemand = left
        io.rightDemand = right
    }

    fun setVelocity(
            leftVel: Double, rightVel: Double, // m/s
            leftAcc: Double = 0.0, rightAcc: Double = 0.0 // m/s^2 * kA
    ) {
        io.driveControlMode = ControlMode.Velocity
        io.leftDemand = leftVel * DriveConstants.kTicksPerMeterPer100ms
        io.rightDemand = rightVel * DriveConstants.kTicksPerMeterPer100ms
        io.leftFeedforward = leftAcc / 1023
        io.rightFeedforward = rightAcc / 1023
    }

    fun setFeedforward(dynamicState: DynamicState) {
        io.driveControlMode = ControlMode.PercentOutput
        io.leftDemand = 0.0
        io.rightDemand = 0.0
        io.leftFeedforward = dynamicState.voltage.left / 12
        io.rightFeedforward = dynamicState.voltage.right / 12
    }

    fun setFeedforward(velocity: ChassisState, acceleration: ChassisState) {
        setFeedforward(model.solve(velocity, acceleration))
    }

    fun setDynamicState(dynamicState: DynamicState) {
        io.driveControlMode = ControlMode.Velocity
        io.leftDemand = dynamicState.velocity.left * DriveConstants.kTicksPerMeterPer100ms
        io.rightDemand = dynamicState.velocity.right * DriveConstants.kTicksPerMeterPer100ms
        io.leftFeedforward = dynamicState.voltage.left / 12
        io.rightFeedforward = dynamicState.voltage.right / 12
    }

    fun setDynamicState(velocity: ChassisState, acceleration: ChassisState) {
        setDynamicState(model.solve(velocity, acceleration))
    }

    fun setAdjustedChassisState(dynamics: DynamicState, adjustedLinear: Double, adjustedAngular: Double) {
        val (adjustedLeft, adjustedRight) = model.solve(ChassisState(adjustedLinear, adjustedAngular))
        val leftVoltage = dynamics.voltage.left + (adjustedLeft - dynamics.velocity.left) / model.speedPerVolt
        val rightVoltage = dynamics.voltage.right + (adjustedRight - dynamics.velocity.right) / model.speedPerVolt
        setDynamicState(DynamicState(WheelState(leftVoltage, rightVoltage), dynamics.velocity))
    }

    private var quickStopAccumulator = 0.0 // gain for stopping from quick turn
    private const val kQuickStopAlpha = 0.1

    fun updateCurvatureDrive(xSpeed: Double, zRotation: Double, isQuickTurn: Boolean) {
        val angularPower: Double

        if (isQuickTurn) {
            if (Math.abs(xSpeed) < 0.2) {
                quickStopAccumulator = (1 - kQuickStopAlpha) * quickStopAccumulator + kQuickStopAlpha * zRotation * 2.0
            }
            angularPower = (zRotation * zRotation + model.frictionVoltage / 12.0)
                    .withSign(zRotation).coerceIn(-0.8, 0.8)
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

        setPercent(leftMotorOutput, rightMotorOutput)
    }

    var robotState: Pose2D = Pose2D.identity
    var chassisVelocity = ChassisState(0.0, 0.0)

    private val dLeft = Delta()
    private val dRight = Delta()

    fun updateRobotStateEstimation() {
        // convert rad/s into m/s
        val wheelVelocity = WheelState(
                io.leftVelocity * model.wheelRadius,
                io.rightVelocity * model.wheelRadius
        )
        // solve into chassis velocity
        chassisVelocity = model.solve(wheelVelocity)
        // If gyro connected, use the yaw value from the gyro as the new angle
        // otherwise add the calculated angular velocity to current yaw
        val dTheta =
                if (io.gyroConnected) io.yaw - io.previousYaw
                else Rotation2D.fromRadians(io.dt * chassisVelocity.angular)
        val theta = robotState.rotation + dTheta
        // find the linear distance traveled by the chassis
        val arcDistance = (dLeft.update(io.leftPosition) + dRight.update(io.rightPosition)) * model.wheelRadius / 2
        // reduce the extra encoder distance when driving in an arc
        val dThetaRad = dTheta.radians
        val chordDistance =
                if (dThetaRad.epsilonEquals(0.01)) arcDistance
                else sin(dThetaRad / 2) * arcDistance / dThetaRad * 2
        // add displacement into current position
        val pos = robotState.translation + theta.translation * chordDistance
        // update the robot state
        robotState = Pose2D(pos, theta)
    }

    private var t = 0.0
    private var trajectory: List<TrajectoryPoint> = listOf()
    private var totalTime = 0.0
    private var direction = 0.0
    private var initialState: Pose2D = Pose2D.identity

    fun initTrajectory(waypoints: Array<Pose2D>, vRatio: Double, aRatio: Double,
                       backwards: Boolean, insertRobotState: Boolean, resetInitialState: Boolean) {
        // resolve ratios and direction
        val maxVelocity = model.maxVelocity * vRatio
        val maxAcceleration = model.maxAcceleration * aRatio
        direction = if (backwards) -1.0 else 1.0
        // make path based on insertRobotState
        val path = if (insertRobotState) quinticSplinesOf(robotState, *waypoints) else quinticSplinesOf(*waypoints)
        // distance-parameterize, then time-parameterize the path into a trajectory
        trajectory = path.parameterized().timedTrajectory(
                model, 0.0, 0.0, maxVelocity, maxAcceleration)
        // reset tracking state
        totalTime = trajectory.last().t
        t = 0.0
        initialState = if (resetInitialState) trajectory.first().state.state else robotState
        previousVelocity = ChassisState(0.0, 0.0)
        neutralOutput()
        io.drivePID = DriveConstants.kTrajectoryPID
    }

    fun getError(setpoint: Pose2D): Pose2D {
        val theta = robotState.rotation - initialState.rotation
        return Pose2D((setpoint.translation - (robotState.translation - initialState.translation)
                .rotate(-initialState.rotation)).rotate(-theta), setpoint.rotation - theta)
    }

    fun advanceTrajectory(dt: Double): TrajectoryPoint {
        t += dt
        var index = 0
        while (index < trajectory.size - 2 && trajectory[index + 1].t < t) index++
        val last = trajectory[index]
        val next = trajectory[index + 1]
        val x = if (last.t.epsilonEquals(next.t)) 1.0 else (t - last.t) / (next.t - last.t)
        val a = direction * interpolate(last.acceleration, next.acceleration, x)
        val v = direction * last.velocity + a * (t - last.t)
        val k = interpolate(last.state.curvature, next.state.curvature, x)
        val p = last.state.state.translation.interpolate(next.state.state.translation, x)
        val h = last.state.state.rotation.interpolate(next.state.state.rotation, x)
        return TrajectoryPoint(CurvatureState(Pose2D(p, h), k, 0.0), v, a, t)
    }

    fun isDoneTrajectory(): Boolean {
        return t > totalTime
    }

    private const val kX = 5.0
    private const val kY = 1.0
    private const val kTheta = 5.0

    fun updatePosePID(error: Pose2D, velocity: ChassisState, acceleration: ChassisState) {
        val adjustedLinear = velocity.linear +
                kX * error.translation.x
        val adjustedAngular = velocity.angular +
                velocity.linear * kY * error.translation.y +
                kTheta * error.rotation.radians
        setAdjustedChassisState(model.solve(velocity, acceleration), adjustedLinear, adjustedAngular)
    }

    private const val kW = 5.0

    fun updateAnglePID(velocity: ChassisState, acceleration: ChassisState) {
        val error = velocity.angular - io.angularVelocity
        val adjustedLinear = velocity.linear
        val adjustedAngular = velocity.angular +
                velocity.linear * kW * error
        setAdjustedChassisState(model.solve(velocity, acceleration), adjustedLinear, adjustedAngular)
    }

    // Equation 5.12 from https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
    private const val kBeta = 2.0  // Correction coefficient, β > 0
    private const val kZeta = 0.7  // Damping coefficient, 0 < ζ < 1

    private var previousVelocity = ChassisState(0.0, 0.0)

    fun updateRamsete(error: Pose2D, velocity: ChassisState) {
        val k = 2.0 * kZeta * Math.sqrt(kBeta * velocity.linear.pow(2) + velocity.angular.pow(2)) // gain
        val turnError = error.rotation.radians
        val sinRatio = if (turnError.epsilonEquals(0.0, 1E-2)) 1.0 else error.rotation.sin / turnError
        val adjustedLinear = velocity.linear * error.rotation.cos + // current linear velocity
                k * error.translation.x // forward error correction
        val adjustedAngular = velocity.angular + // current angular velocity
                k * turnError + // turn error correction
                velocity.linear * kBeta * sinRatio * error.translation.y // lateral error correction
        val adjustedVelocity = ChassisState(adjustedLinear, adjustedAngular)
        previousVelocity = adjustedVelocity
        // re-calculate acceleration
        val linearAcc = (adjustedLinear - previousVelocity.linear) / io.dt
        val angularAcc = (adjustedAngular - previousVelocity.angular) / io.dt
        val adjustedAcceleration = ChassisState(linearAcc, angularAcc)
        setDynamicState(adjustedVelocity, adjustedAcceleration)
    }
}