package ca.warp7.frckt

import edu.wpi.first.wpilibj.GenericHID.Hand.kLeft
import edu.wpi.first.wpilibj.GenericHID.Hand.kRight
import edu.wpi.first.wpilibj.XboxController

enum class ControllerState {
    Pressed, Released, HeldDown, KeptUp
}

internal fun u(old: ControllerState, _new: Boolean): ControllerState {
    return if (_new)
        if (old == ControllerState.Pressed || old == ControllerState.HeldDown) ControllerState.HeldDown else ControllerState.Pressed
    else if (old == ControllerState.Released || old == ControllerState.KeptUp) ControllerState.KeptUp else ControllerState.Released
}

internal fun collectControllerData(s: ControllerData, c: XboxController) {
    s.leftTriggerAxis = c.getTriggerAxis(kLeft)
    s.rightTriggerAxis = c.getTriggerAxis(kRight)
    s.leftXAxis = c.getX(kLeft)
    s.leftYAxis = c.getY(kLeft)
    s.rightXAxis = c.getX(kRight)
    s.rightYAxis = c.getY(kRight)
    s.aButton = u(s.aButton, c.aButton)
    s.bButton = u(s.bButton, c.bButton)
    s.xButton = u(s.xButton, c.xButton)
    s.yButton = u(s.yButton, c.yButton)
    s.leftBumper = u(s.leftBumper, c.getBumper(kLeft))
    s.rightBumper = u(s.rightBumper, c.getBumper(kRight))
    s.leftStickButton = u(s.leftStickButton, c.getStickButton(kLeft))
    s.rightStickButton = u(s.rightStickButton, c.getStickButton(kRight))
    s.startButton = u(s.startButton, c.startButton)
    s.backButton = u(s.backButton, c.backButton)
}