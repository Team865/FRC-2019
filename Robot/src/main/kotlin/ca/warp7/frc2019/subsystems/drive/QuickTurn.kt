package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.geometry.radians
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure
import com.ctre.phoenix.motorcontrol.ControlMode

class QuickTurn(angleInDegrees: Double) : Action {

    private val targetYaw = Math.toRadians(angleInDegrees)
    private var startYaw = 0.0
    private var error = targetYaw
    private var dError = 0.0

    override fun start() {
        Drive.controlMode = ControlMode.PercentOutput
        startYaw = Infrastructure.yaw.radians
    }

    override fun update() {
        val newError = targetYaw - Infrastructure.yaw.radians + startYaw
        dError = (newError - error) * DriveMotionPlanner.lastDt
        error = newError
        val angularKp = 0.05
        val angularKd = 1.0
        val angularGain = error * angularKp + dError * angularKd
        Drive.leftDemand = -angularGain
        Drive.rightDemand = angularGain
    }

    override val shouldFinish: Boolean
        get() = error < 0.05 && dError < 0.1

    override fun stop() {
        Drive.apply {
            leftDemand = 0.0
            rightDemand = 0.0
            leftFeedforward = 0.0
            rightFeedforward = 0.0
        }
    }
}