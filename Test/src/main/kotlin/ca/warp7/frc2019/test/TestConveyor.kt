package ca.warp7.frc2019.test

import ca.warp7.frc2019.constants.ConveyorConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController

class TestConveyor : TimedRobot(){
    lateinit var controller: XboxController
    lateinit var leftConveyor : VictorSPX
    lateinit var rightConveyor : VictorSPX
    override fun robotInit() {
        leftConveyor = VictorSPX (ConveyorConstants.kLeft)
        rightConveyor = VictorSPX(ConveyorConstants.kRight)
        controller = XboxController(0 )
    }
    override fun teleopPeriodic() {
        val input = controller.getTriggerAxis(GenericHID.Hand.kLeft)
        if (input>0.1){
            rightConveyor.set (ControlMode.PercentOutput,input)
            leftConveyor.set (ControlMode.PercentOutput,input*-1)
        }else{
            rightConveyor.set (ControlMode.PercentOutput,controller.getTriggerAxis(GenericHID.Hand.kRight))
            leftConveyor.set (ControlMode.PercentOutput,(controller.getTriggerAxis(GenericHID.Hand.kRight))*-1)

        }
    }
}