package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import edu.wpi.first.wpilibj.Solenoid

object Climber : Subsystem() {
    private val solenoid = Solenoid(0)

    var climbing = false

    override fun onOutput() {
        solenoid.set(climbing)
    }
}