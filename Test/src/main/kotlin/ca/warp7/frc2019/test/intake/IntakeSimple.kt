package ca.warp7.frc2019.test.intake

import ca.warp7.frc2019.constants.IntakeConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController

@Suppress("unused")
class IntakeSimple : TimedRobot() {
    lateinit var victor : VictorSPX
    lateinit var controller : XboxController
    lateinit var solenoid : Solenoid

    override fun robotInit(){
        victor = VictorSPX(IntakeConstants.kVictor)
        controller = XboxController(0)
        solenoid = Solenoid(IntakeConstants.kSolenoid)
    }

    override fun teleopPeriodic() {
        victor.set(ControlMode.PercentOutput,controller.getTriggerAxis(GenericHID.Hand.kRight))
        solenoid.set(controller.bButton)
    }
}