package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.BackIntakeConstants
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object Intake : Subsystem() {

    private val victor = VictorSPX(BackIntakeConstants.kVictor)

    override fun onDisabled() {
        victor.neutralOutput()
    }

    override fun onOutput() {
    }
}