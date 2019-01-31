package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.OuttakeConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.Solenoid

object Hatch : Subsystem() {

    private val pusher = Solenoid(OuttakeConstants.kSolenoid)

    var pushing = false

    override fun onDisabled() {}

    override fun onOutput() {
        pusher.set(pushing)
    }
}