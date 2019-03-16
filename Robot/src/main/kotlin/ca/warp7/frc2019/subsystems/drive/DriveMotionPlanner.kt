package ca.warp7.frc2019.subsystems.drive

import ca.warp7.frc.Subsystem
import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused", "MemberVisibilityCanBePrivate")
object DriveMotionPlanner : Subsystem() {

    private val velocityHz = 1000 / DriveConstants.kMasterTalonConfig.velocityMeasurementPeriod.value

    var lastDt = 0.0
    val motionState = DriveMotionState()
    val leftPositionInches get() = Drive.leftPosition / DriveConstants.kTicksPerInch
    val rightPositionInches get() = Drive.rightPosition / DriveConstants.kTicksPerInch
    val leftVelocityInches get() = (leftVelocity / 2) * DriveConstants.kWheelDiameter
    val rightVelocityInches get() = (rightVelocity / 2) * DriveConstants.kWheelDiameter

    var leftVelocity = 0.0 // rad/s
    var rightVelocity = 0.0 // rad/s


    val model = DifferentialDriveModel(
            wheelbaseRadius = DriveConstants.kEffectiveWheelBaseRadius,
            maxVelocity = DriveConstants.kMaxVelocity,
            maxAcceleration = DriveConstants.kMaxAcceleration,
            maxFreeSpeedVelocity = DriveConstants.kMaxFreeSpeedVelocity,
            frictionVoltage = DriveConstants.kVIntercept
    )

    fun updateMeasurements(dt: Double) {
        lastDt = dt
        leftVelocity = Drive.leftVelocity / DriveConstants.kTicksPerRevolution * 2 * Math.PI * velocityHz
        rightVelocity = Drive.rightVelocity / DriveConstants.kTicksPerRevolution * 2 * Math.PI * velocityHz
        val measuredYaw = Infrastructure.yaw
        val measuredVelocity = (leftVelocityInches + rightVelocityInches) / 2
        motionState.apply {
            x += measuredVelocity * cos(measuredYaw) * dt
            y += measuredVelocity * sin(measuredYaw) * dt
            yaw = measuredYaw
            vel = measuredVelocity
        }
    }

    override fun onPostUpdate() {
        put("State.X", motionState.x)
        put("State.Y", motionState.y)
        put("State.Yaw", motionState.yaw)
        put("State.Vel", motionState.vel)
    }
}