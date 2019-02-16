package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.lazySolenoid
import ca.warp7.frc2019.constants.ClimberConstants
import edu.wpi.first.wpilibj.Solenoid

object Climber : Subsystem() {
    private val solenoid: Solenoid = lazySolenoid(ClimberConstants.kSolenoid)

    var climbing = false

    override fun onOutput() {
        solenoid.set(climbing)
    }
}