package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.lazySolenoid
import ca.warp7.frc.victorSPX
import ca.warp7.frc2019.constants.HatchConstants
import ca.warp7.frc2019.constants.OuttakeConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.Solenoid

object Outtake : Subsystem() {

    private val left: VictorSPX = victorSPX(OuttakeConstants.kLeft)
    private val right: VictorSPX = victorSPX(OuttakeConstants.kRight)
    private val pusher: Solenoid = lazySolenoid(HatchConstants.kPusherSolenoid)
    private val grabber: Solenoid = lazySolenoid(HatchConstants.kGrabberSolenoid)

    init {
        left.setNeutralMode(NeutralMode.Brake)
        right.setNeutralMode(NeutralMode.Brake)
    }

    var speed = 0.0
    var pushing = false
    var grabbing = false

    override fun onDisabled() {
        left.neutralOutput()
        right.neutralOutput()

        pusher.set(false)
        grabber.set(false)
    }

    override fun onOutput() {
        left.set(ControlMode.PercentOutput, speed)
        right.set(ControlMode.PercentOutput, -speed)
        pusher.set(pushing)
        grabber.set(grabbing)
    }
}