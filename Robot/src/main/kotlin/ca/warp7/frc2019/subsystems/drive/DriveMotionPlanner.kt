package ca.warp7.frc2019.subsystems.drive

import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.drive.WheelState
import ca.warp7.frc.drive.solve
import ca.warp7.frc.feetToMeters
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.fromRadians
import ca.warp7.frc.geometry.translation
import ca.warp7.frc.inchesToMeters
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure

@Suppress("unused", "MemberVisibilityCanBePrivate")
object DriveMotionPlanner {

    var dt = 0.0
    var robotState: Pose2D = Pose2D.identity

    var leftVelocity = 0.0 // rad/s
    var rightVelocity = 0.0 // rad/s

    var wheelVelocity = WheelState(0.0, 0.0) // m/s
    var chassisVelocity = ChassisState(0.0, 0.0) // m/s and rad/s

    val model = DifferentialDriveModel(
            wheelRadius = inchesToMeters(DriveConstants.kWheelRadius),
            wheelbaseRadius = inchesToMeters(DriveConstants.kEffectiveWheelBaseRadius),
            maxVelocity = feetToMeters(DriveConstants.kMaxVelocity),
            maxAcceleration = feetToMeters(DriveConstants.kMaxAcceleration),
            maxFreeSpeed = feetToMeters(DriveConstants.kMaxFreeSpeedVelocity),
            speedPerVolt = feetToMeters(DriveConstants.kSpeedPerVolt),
            torquePerVolt = DriveConstants.kTorquePerVolt,
            frictionVolts = DriveConstants.kVIntercept,
            linearInertia = DriveConstants.kLinearInertia,
            angularInertia = DriveConstants.kAngularInertia,
            maxVolts = DriveConstants.kMaxVolts
    )

    fun updateMeasurements(newDt: Double) {
        dt = newDt

        // convert ticks/100ms into rad/s
        leftVelocity = Drive.leftVelocity / DriveConstants.kTicksPerRevolution * 2 * Math.PI * 10
        rightVelocity = Drive.rightVelocity / DriveConstants.kTicksPerRevolution * 2 * Math.PI * 10

        // convert rad/s into m/s
        wheelVelocity = WheelState(left = leftVelocity * model.wheelRadius, right = rightVelocity * model.wheelRadius)

        // solve into chassis velocity
        chassisVelocity = model.solve(wheelVelocity)

        updateLocalization()
    }

    fun updateLocalization() {
        // If gyro connected, use the yaw value from the gyro as the new angle
        // otherwise add the calculated angular velocity to current yaw
        val newAngle =
                if (Infrastructure.ahrsCalibrated) Infrastructure.yaw
                else robotState.rotation + Rotation2D.fromRadians(chassisVelocity.angular * dt)

        // add displacement into current position
        val newPosition = robotState.translation + newAngle.translation * (chassisVelocity.linear * dt)

        // update the robot state
        robotState = Pose2D(newPosition, newAngle)
    }
}