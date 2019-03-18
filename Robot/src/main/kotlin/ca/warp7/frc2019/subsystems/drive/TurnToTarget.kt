package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Limelight
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.withSign


class TurnToTarget : Action {
    var error = 0.0

    override fun start() {
        Drive.controlMode = ControlMode.PercentOutput
    }

    override fun update() {
        if (!Limelight.hasTarget) return
        error = Math.toRadians(Limelight.x)
        if (error.epsilonEquals(0.0, 0.02)) {
            Drive.leftDemand = 0.0
            Drive.rightDemand = 0.0
            return
        }
        val kP = 0.5
        val kVi = 0.2
        val frictionVoltage = kVi.withSign(error)
        Drive.leftDemand = error * kP + frictionVoltage
        Drive.rightDemand = -error * kP - frictionVoltage
    }

    override val shouldFinish = false
}