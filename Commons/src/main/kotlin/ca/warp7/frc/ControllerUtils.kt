package ca.warp7.frc

import ca.warp7.frc.ControllerState.*
import edu.wpi.first.wpilibj.GenericHID.Hand.kLeft
import edu.wpi.first.wpilibj.GenericHID.Hand.kRight
import edu.wpi.first.wpilibj.XboxController


internal fun u(old: ControllerState, _new: Boolean): ControllerState {
    return if (_new)
        if (old == Pressed || old == HeldDown) HeldDown else Pressed
    else if (old == Released || old == None) None else Released
}

internal fun RobotControllerImpl.updateWith(c: XboxController) {
    leftTriggerAxis = c.getTriggerAxis(kLeft)
    rightTriggerAxis = c.getTriggerAxis(kRight)
    leftXAxis = c.getX(kLeft)
    leftYAxis = c.getY(kLeft)
    rightXAxis = c.getX(kRight)
    rightYAxis = c.getY(kRight)
    aButton = u(aButton, c.aButton)
    bButton = u(bButton, c.bButton)
    xButton = u(xButton, c.xButton)
    yButton = u(yButton, c.yButton)
    leftBumper = u(leftBumper, c.getBumper(kLeft))
    rightBumper = u(rightBumper, c.getBumper(kRight))
    leftStickButton = u(leftStickButton, c.getStickButton(kLeft))
    rightStickButton = u(rightStickButton, c.getStickButton(kRight))
    startButton = u(startButton, c.startButton)
    backButton = u(backButton, c.backButton)

}

internal fun RobotControllerImpl.reset() {
    leftTriggerAxis = 0.0
    rightTriggerAxis = 0.0
    leftXAxis = 0.0
    leftYAxis = 0.0
    rightXAxis = 0.0
    rightYAxis = 0.0
    aButton = None
    bButton = None
    xButton = None
    yButton = None
    leftBumper = None
    rightBumper = None
    leftStickButton = None
    rightStickButton = None
    startButton = None
    backButton = None

}