package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.FrontIntakeConstants
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object FrontIntake : Subsystem() {

    private val left = VictorSPX(FrontIntakeConstants.kLeft)
    private val right = VictorSPX(FrontIntakeConstants.kRight)

    override fun onDisabled() {
        left.neutralOutput()
        right.neutralOutput()
    }

    override fun onOutput() {
    }
}