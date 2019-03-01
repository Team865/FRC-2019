package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.lazySolenoid
import ca.warp7.frc.victorSPX
import ca.warp7.frc2019.constants.IntakeConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.Solenoid

object Intake : Subsystem() {

    private val master: VictorSPX = victorSPX(IntakeConstants.kVictor, neutralMode = NeutralMode.Coast)
    private val solenoid: Solenoid = lazySolenoid(IntakeConstants.kSolenoid)

    var extended = false
    var speed = 0.0

    override fun onDisabled() {
        master.neutralOutput()
    }

    override fun onOutput() {
        master.set(ControlMode.PercentOutput, speed)
        solenoid.set(extended)
    }
}