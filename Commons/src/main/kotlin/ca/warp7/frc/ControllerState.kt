package ca.warp7.frc

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
    with(s) {
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
}