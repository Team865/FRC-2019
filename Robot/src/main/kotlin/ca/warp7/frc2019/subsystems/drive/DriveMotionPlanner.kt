package ca.warp7.frc2019.subsystems.drive

import ca.warp7.frc.Subsystem
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
    val leftPositionInches get() = Drive.leftPositionTicks / DriveConstants.kTicksPerInch
    val rightPositionInches get() = Drive.rightPositionTicks / DriveConstants.kTicksPerInch
    val leftVelocityInches get() = (leftVelocity / 2) * DriveConstants.kWheelDiameter
    val rightVelocityInches get() = (rightVelocity / 2) * DriveConstants.kWheelDiameter

    var leftVelocity = 0.0 // rad/s
    var rightVelocity = 0.0 // rad/s

    fun updateMeasurements(dt: Double) {
        lastDt = dt
        leftVelocity = Drive.leftVelocityTicks / DriveConstants.kTicksPerRevolution * 2 * Math.PI * velocityHz
        rightVelocity = Drive.rightVelocityTicks / DriveConstants.kTicksPerRevolution * 2 * Math.PI * velocityHz
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