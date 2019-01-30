package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.IntakeConstants
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.Solenoid

object Intake : Subsystem() {

    val victor = VictorSPX(IntakeConstants.kVictor)
    val solenoid = Solenoid(IntakeConstants.kVictor)

    override fun onDisabled() {
        victor.neutralOutput()
    }

    override fun onOutput() {
    }
}