package ca.warp7.frc2019.subsystems

import ca.warp7.frc.PID
import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.drive.DynamicState
import ca.warp7.frc.drive.WheelState
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.CurvatureState
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.radians
import ca.warp7.frc.geometry.rotate
import ca.warp7.frc.interpolate
import ca.warp7.frc.path.parameterized
import ca.warp7.frc.path.quinticSplinesOf
import ca.warp7.frc.trajectory.TrajectoryPoint
import ca.warp7.frc.trajectory.timedTrajectory
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

object Drive {

    private val io: RobotIO = RobotIO

    var robotState: Pose2D = Pose2D.identity
    var chassisVelocity = ChassisState(0.0, 0.0)

    val model = DifferentialDriveModel(
            wheelRadius = DriveConstants.kWheelRadius,
            wheelbaseRadius = DriveConstants.kEffectiveWheelBaseRadius,
            maxVelocity = DriveConstants.kMaxVelocity,
            maxAcceleration = DriveConstants.kMaxAcceleration,
            maxFreeSpeed = DriveConstants.kMaxFreeSpeed,
            speedPerVolt = DriveConstants.kSpeedPerVolt,
            torquePerVolt = DriveConstants.kTorquePerVolt,
            frictionVoltage = DriveConstants.kFrictionVoltage,
            linearInertia = DriveConstants.kLinearInertia,
            angularInertia = DriveConstants.kAngularInertia,
            maxVoltage = DriveConstants.kMaxVolts,
            angularDrag = DriveConstants.kAngularDrag
    )

    fun neutralOutput() {
        io.driveControlMode = ControlMode.Disabled
        io.leftDemand = 0.0
        io.rightDemand = 0.0
        io.leftFeedforward = 0.0
        io.rightFeedforward = 0.0
    }

    fun setNormalize(left: Double, right: Double) {
        var mag = max(abs(left), abs(right))
        if (mag <= 1.0) {
            mag = 1.0
        }
        setPercent(left / mag, right / mag)
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


    // Equation 5.12 from https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
    private const val kBeta = 2.0  // Correction coefficient, β > 0
    private const val kZeta = 0.7  // Damping coefficient, 0 < ζ < 1

    private var previousVelocity = ChassisState(0.0, 0.0)

    fun updateRamsete(error: Pose2D, velocity: ChassisState) {

        val k = 2.0 * kZeta * Math.sqrt(kBeta * velocity.linear.pow(2) + velocity.angular.pow(2))

        val angularError = error.rotation.radians
        val sinXOverX =
                if (angularError.epsilonEquals(0.0, 1E-2)) 1.0
                else error.rotation.sin / angularError

        val linearVel = velocity.linear * error.rotation.cos + // current linear velocity
                k * error.translation.x // forward error correction

        val angularVel = velocity.angular + // current angular velocity
                k * angularError + // angular error correction
                velocity.linear * kBeta * sinXOverX * error.translation.y // lateral error correction

        val adjustedVelocity = ChassisState(linearVel, angularVel)
        previousVelocity = adjustedVelocity

        val linearAcc = (linearVel - previousVelocity.linear) / io.dt
        val angularAcc = (angularVel - previousVelocity.angular) / io.dt
        val adjustedAcceleration = ChassisState(linearAcc, angularAcc)

        setDynamicState(adjustedVelocity, adjustedAcceleration)
    }

    private const val kX = 5.0
    private const val kY = 1.0
    private const val kTheta = 5.0

    fun updatePID(error: Pose2D, velocity: ChassisState, acceleration: ChassisState) {

        val linear = velocity.linear + kX * error.translation.x
        val angular = velocity.angular + velocity.linear * kY * error.translation.y + kTheta * error.rotation.radians

        setAdjustedChassisState(model.solve(velocity, acceleration), linear, angular)
    }

    private const val kW = 5.0

    fun updateAnglePID(velocity: ChassisState, acceleration: ChassisState) {

        val error = velocity.angular - io.angularVelocity
        val linear = velocity.linear
        val angular = velocity.angular + velocity.linear * kW * error

        setAdjustedChassisState(model.solve(velocity, acceleration), linear, angular)
    }

    private var t = 0.0
    private var trajectory: List<TrajectoryPoint> = listOf()
    private var totalTime = 0.0
    private var direction = 0.0
    private var initialState: Pose2D = Pose2D.identity

    fun initTrajectory(waypoints: Array<Pose2D>, vRatio: Double, aRatio: Double,
                       backwards: Boolean, insertRobotState: Boolean, resetInitialState: Boolean) {
        val maxVelocity = model.maxVelocity * vRatio
        val maxAcceleration = model.maxAcceleration * aRatio
        val path = if (insertRobotState) quinticSplinesOf(robotState, *waypoints) else quinticSplinesOf(*waypoints)
        trajectory = path.parameterized().timedTrajectory(
                model, 0.0, 0.0, maxVelocity, maxAcceleration)
        direction = if (backwards) -1.0 else 1.0
        totalTime = trajectory.last().t
        t = 0.0
        initialState = if (resetInitialState) trajectory.first().state.state else robotState
        previousVelocity = ChassisState(0.0, 0.0)
        neutralOutput()
        io.drivePID = PID(kP = 0.8, kI = 0.0, kD = 5.0)
    }

    fun getError(setpoint: Pose2D): Pose2D {
        val pathToRobot = (robotState.translation - initialState.translation).rotate(-initialState.rotation)
        val inverseTheta = initialState.rotation - robotState.rotation
        return Pose2D((setpoint.translation - pathToRobot).rotate(inverseTheta), (setpoint.rotation + inverseTheta))
    }

    fun advanceTrajectory(dt: Double): TrajectoryPoint {
        t += dt
        var index = 0
        while (index < trajectory.size - 2 && trajectory[index + 1].t < t) index++
        val last = trajectory[index]
        val next = trajectory[index + 1]

        val x = if (last.t.epsilonEquals(next.t)) 1.0 else (t - last.t) / (next.t - last.t)
        val a = interpolate(last.acceleration, next.acceleration, x)
        val v = last.velocity + a * (t - last.t)
        val k = interpolate(last.state.curvature, next.state.curvature, x)

        val p = last.state.state.translation.interpolate(next.state.state.translation, x)
        val h = last.state.state.rotation.interpolate(next.state.state.rotation, x)

        return TrajectoryPoint(CurvatureState(Pose2D(p, h), k, 0.0), v, a, t)
    }

    fun isDoneTrajectory(): Boolean {
        return t > totalTime
    }
}