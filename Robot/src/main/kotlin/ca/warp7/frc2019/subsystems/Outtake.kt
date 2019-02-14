package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.OuttakeConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object Outtake : Subsystem() {

    private val left = VictorSPX(OuttakeConstants.kLeft)
    private val right = VictorSPX(OuttakeConstants.kRight)

    init {
        left.setNeutralMode(NeutralMode.Brake)
        right.setNeutralMode(NeutralMode.Brake)
    }

    var speed = 0.0

    override fun onDisabled() {
        left.neutralOutput()
        right.neutralOutput()
    }

    override fun onOutput() {
        left.set(ControlMode.PercentOutput, speed)
        right.set(ControlMode.PercentOutput, speed)
    }
}