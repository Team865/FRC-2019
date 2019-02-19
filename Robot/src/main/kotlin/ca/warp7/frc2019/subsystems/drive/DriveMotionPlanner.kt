package ca.warp7.frc2019.subsystems.drive

import ca.warp7.frc.Subsystem
import ca.warp7.frc.set
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused", "MemberVisibilityCanBePrivate")
object DriveMotionPlanner : Subsystem() {

    private val measurementFrequency = 1000 / LiftConstants.kMasterTalonConfig.velocityMeasurementPeriod.value

    var lastDt = 0.0
    private val motionState = DriveMotionState()
    val leftPositionInches get() = Drive.leftPositionTicks / DriveConstants.kTicksPerInch
    val rightPositionInches get() = Drive.rightPositionTicks / DriveConstants.kTicksPerInch
    val leftVelocityInches
        get() =
            Drive.leftVelocityTicks / DriveConstants.kTicksPerInch * measurementFrequency
    val rightVelocityInches
        get() =
            Drive.rightVelocityTicks / DriveConstants.kTicksPerInch * measurementFrequency

    init {
        set { }
    }

    fun updateMeasurements(dt: Double) {
        lastDt = dt
        val measuredYaw = Infrastructure.yaw
        val measuredVelocity = (leftVelocityInches + rightPositionInches) / 2
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