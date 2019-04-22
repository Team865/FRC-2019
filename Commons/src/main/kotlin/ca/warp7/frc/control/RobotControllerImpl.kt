package ca.warp7.frc.control

import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.XboxController

internal class RobotControllerImpl : RobotController {
    override var backButton = ControllerState.None
    override var aButton = ControllerState.None
    override var bButton = ControllerState.None
    override var xButton = ControllerState.None
    override var yButton = ControllerState.None
    override var leftBumper = ControllerState.None
    override var rightBumper = ControllerState.None
    override var leftStickButton = ControllerState.None
    override var rightStickButton = ControllerState.None
    override var startButton = ControllerState.None
    override var leftTriggerAxis = 0.0
    override var rightTriggerAxis = 0.0
    override var leftXAxis = 0.0
    override var leftYAxis = 0.0
    override var rightXAxis = 0.0
    override var rightYAxis = 0.0

    fun updateWith(c: XboxController) {
        leftTriggerAxis = c.getTriggerAxis(GenericHID.Hand.kLeft)
        rightTriggerAxis = c.getTriggerAxis(GenericHID.Hand.kRight)
        leftXAxis = c.getX(GenericHID.Hand.kLeft)
        leftYAxis = c.getY(GenericHID.Hand.kLeft)
        rightXAxis = c.getX(GenericHID.Hand.kRight)
        rightYAxis = c.getY(GenericHID.Hand.kRight)
        aButton = u(aButton, c.aButton)
        bButton = u(bButton, c.bButton)
        xButton = u(xButton, c.xButton)
        yButton = u(yButton, c.yButton)
        leftBumper = u(leftBumper, c.getBumper(GenericHID.Hand.kLeft))
        rightBumper = u(rightBumper, c.getBumper(GenericHID.Hand.kRight))
        leftStickButton = u(leftStickButton, c.getStickButton(GenericHID.Hand.kLeft))
        rightStickButton = u(rightStickButton, c.getStickButton(GenericHID.Hand.kRight))
        startButton = u(startButton, c.startButton)
        backButton = u(backButton, c.backButton)
    }

    fun reset() {
        leftTriggerAxis = 0.0
        rightTriggerAxis = 0.0
        leftXAxis = 0.0
        leftYAxis = 0.0
        rightXAxis = 0.0
        rightYAxis = 0.0
        aButton = ControllerState.None
        bButton = ControllerState.None
        xButton = ControllerState.None
        yButton = ControllerState.None
        leftBumper = ControllerState.None
        rightBumper = ControllerState.None
        leftStickButton = ControllerState.None
        rightStickButton = ControllerState.None
        startButton = ControllerState.None
        backButton = ControllerState.None
    }

    fun u(old: ControllerState, _new: Boolean) =
            if (_new)
                if (old == ControllerState.Pressed || old == ControllerState.HeldDown)
                    ControllerState.HeldDown
                else
                    ControllerState.Pressed
            else
                if (old == ControllerState.Released || old == ControllerState.None)
                    ControllerState.None
                else
                    ControllerState.Released
}

