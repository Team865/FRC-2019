package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.lazySolenoid
import ca.warp7.frc.lazyVictorSPX
import ca.warp7.frc2019.constants.IntakeConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.Solenoid

object Intake : Subsystem() {

    private val victor: VictorSPX = lazyVictorSPX(IntakeConstants.kVictor)
    private val solenoid: Solenoid = lazySolenoid(IntakeConstants.kSolenoid)

    init {
        victor.setNeutralMode(NeutralMode.Coast)
    }

    var extended = false
    var speed = 0.0

    override fun onDisabled() {
        victor.neutralOutput()
    }

    override fun onOutput() {
        victor.set(ControlMode.PercentOutput, speed)
        solenoid.set(extended)
    }
}