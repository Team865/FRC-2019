package ca.warp7.frc2019.test.lift.feedforward

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.LiftConstants
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object LiftSubsystem : Subsystem() {

    val master = TalonSRX(LiftConstants.kMaster).apply {
        setNeutralMode(NeutralMode.Brake)
        configAllSettings(LiftConstants.kMasterTalonConfig)
        enableVoltageCompensation(true)
        enableCurrentLimit(false)
    }

    init {
        val victor = VictorSPX(LiftConstants.kFollower)
        victor.setNeutralMode(NeutralMode.Brake)
        victor.inverted = true
        victor.follow(master)
    }
}