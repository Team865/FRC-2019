package ca.warp7.frc2019.test.conveyor

import ca.warp7.frc2019.constants.ConveyorConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController

@Suppress("unused")
class ConveyorSimple : TimedRobot() {
    lateinit var controller: XboxController
    lateinit var leftConveyor: VictorSPX
    lateinit var rightConveyor: VictorSPX
    override fun robotInit() {
        leftConveyor = VictorSPX(ConveyorConstants.kLeft)
        rightConveyor = VictorSPX(ConveyorConstants.kRight)
        controller = XboxController(0)
    }

    override fun disabledInit() {
        leftConveyor.neutralOutput()
        rightConveyor.neutralOutput()
    }

    override fun teleopPeriodic() {
        val left = controller.getTriggerAxis(GenericHID.Hand.kLeft)
        val right = controller.getTriggerAxis(GenericHID.Hand.kRight)
        var speed = 0.0
        if (left > 0.1) {
            speed = left
        } else if (right > 0.1) {
            speed = right * -1
        }
        rightConveyor.set(ControlMode.PercentOutput, speed)
        leftConveyor.set(ControlMode.PercentOutput, speed)
    }
}