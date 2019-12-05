package ca.warp7.frc2019.subsystems

import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.drive.DriveOdometry
import ca.warp7.frc.drive.WheelState
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc2019.followers.PosePIDFollower
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.withSign

object Drive {

    // Drive Train Hardware Constants

    const val kTicksPerRadian = 1024 / (2 * PI) // ticks/rad

    private const val kWheelBaseRadius = 0.44 // metres
    private const val kWheelRadius = 3 * 0.0254 // m
    private const val kMaxFreeSpeed = 5.0 // m/s

    const val kTicksPerMeterPer100ms = kTicksPerRadian / kWheelRadius * 0.1 // ticks/m*100ms

    // Dynamic Constants

    private const val kMaxVoltage = 12.0 // V
    private const val kFrictionVoltage = 1.0 // V
    private const val kMass = 60.0 // kg
    private const val kMomentOfInertia = 10.0 // kg * m^2
    private const val kAngularDrag = 0.0 // (N * m) / (rad/s)

    private const val kSpeedPerVolt = kMaxFreeSpeed / kMaxVoltage // (m/s) / V

    private const val kTorquePerVolt = 0.5 * kWheelRadius * kWheelRadius * kMass / 0.012  // (N * m) / V

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

    val odometry = DriveOdometry(Rotation2D.identity, Pose2D.identity)
    val robotState: Pose2D get() = odometry.pose()

    fun updateRobotStateEstimation() {
        odometry.update(io.yaw, io.leftPosition, io.rightPosition)
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
}