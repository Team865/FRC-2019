package ca.warp7.frc2019.subsystems

import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.drive.DynamicState
import ca.warp7.frc.drive.WheelState
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.radians
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

object Drive {

    var robotState: Pose2D = Pose2D.identity
    private val io: RobotIO = RobotIO

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

    fun setDynamicFeedforward(dynamicState: DynamicState) {
        io.driveControlMode = ControlMode.PercentOutput
        io.leftDemand = 0.0
        io.rightDemand = 0.0
        setDynamicFeedforward(dynamicState)
    }

    fun setDynamicState(dynamicState: DynamicState) {
        io.driveControlMode = ControlMode.Velocity
        io.leftDemand = dynamicState.velocity.left * DriveConstants.kTicksPerMeterPer100ms
        io.rightDemand = dynamicState.velocity.right * DriveConstants.kTicksPerMeterPer100ms
        io.leftFeedforward = dynamicState.voltage.left / 12
        io.rightFeedforward = dynamicState.voltage.right / 12
    }

    var prevVelocity = ChassisState(0.0, 0.0)
    // Implements eqn. 5.12 from https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
    const val kBeta = 2.0  // >0.
    const val kZeta = 0.7  // Damping coefficient, [0, 1].

    fun updateRamsete(error: Pose2D, velocity: ChassisState): DynamicState {
        // Compute gain parameter.
        val k = 2.0 * kZeta * Math.sqrt(kBeta * velocity.linear.pow(2) + velocity.angular.pow(2))
        // Compute error components.
        val angularError = error.rotation.radians
        val sinXOverX =
                if (angularError.epsilonEquals(0.0, 1E-2)) 1.0
                else error.rotation.sin / angularError

        val linearVel =
                velocity.linear * error.rotation.cos +
                        k * error.translation.x
        val angularVel =
                velocity.angular +
                        k * angularError +
                        velocity.linear * kBeta * sinXOverX * error.translation.y

        val adjustedVelocity = ChassisState(linearVel, angularVel)
        prevVelocity = adjustedVelocity

        val linearAcc = (linearVel - prevVelocity.linear) / io.dt
        val angularAcc = (angularVel - prevVelocity.angular) / io.dt
        val adjustedAcceleration = ChassisState(linearAcc, angularAcc)

        return model.solve(adjustedVelocity, adjustedAcceleration)
    }

    // Feedback on longitudinal error (distance).
    const val kX = 5.0
    const val kY = 1.0
    const val kTheta = 5.0

    fun updatePID(error: Pose2D, velocity: ChassisState, acceleration: ChassisState): DynamicState {

        val linear = velocity.linear + kX * error.translation.x
        val angular = velocity.angular + velocity.linear * kY * error.translation.y + kTheta * error.rotation.radians

        val dynamics = model.solve(velocity, acceleration)

        val (adjustedLeft, adjustedRight) = model.solve(ChassisState(linear, angular))

        val leftVoltage = dynamics.voltage.left + (adjustedLeft - dynamics.velocity.left) / model.speedPerVolt
        val rightVoltage = dynamics.voltage.right + (adjustedRight - dynamics.velocity.right) / model.speedPerVolt

        return DynamicState(WheelState(leftVoltage, rightVoltage), dynamics.velocity)
    }
}