package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Limelight
import com.ctre.phoenix.motorcontrol.ControlMode


object TurnToTarget: Action {
    var error = 0.0

    override fun start() {
        Drive.controlMode = ControlMode.PercentOutput
    }

    override fun update() {
        if (!Limelight.hasTarget) return
        error=Math.toRadians(Limelight.x)
        val kP = 2
        Drive.leftDemand = error * kP
        Drive.rightDemand = -error * kP
    }

    override val shouldFinish = false
}