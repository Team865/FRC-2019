package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.LiftConstants
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object Lift : Subsystem() {

    private val master = TalonSRX(LiftConstants.kMaster)

    init {
        VictorSPX(LiftConstants.kFollower).follow(master)
    }

    override fun onDisabled() {
        master.neutralOutput()
    }

    override fun onOutput() {
    }
}