package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.HatchConstants
import edu.wpi.first.wpilibj.Solenoid

object Hatch : Subsystem() {

    private val pusher = Solenoid(HatchConstants.kSolenoid)

    var pushing = false

    override fun onDisabled() {
        pusher.set(false)
    }

    override fun onOutput() {
        pusher.set(pushing)
    }
}