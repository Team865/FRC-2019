package ca.warp7.frc

import ca.warp7.frc.ControllerState.*
import edu.wpi.first.wpilibj.GenericHID.Hand.kLeft
import edu.wpi.first.wpilibj.GenericHID.Hand.kRight
import edu.wpi.first.wpilibj.XboxController


internal fun u(old: ControllerState, _new: Boolean): ControllerState {
    return if (_new)
        if (old == Pressed || old == HeldDown) HeldDown else Pressed
    else if (old == Released || old == KeptUp) KeptUp else Released
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

internal fun resetControllerData(s: ControllerData) {
    with(s) {
        leftTriggerAxis = 0.0
        rightTriggerAxis = 0.0
        leftXAxis = 0.0
        leftYAxis = 0.0
        rightXAxis = 0.0
        rightYAxis = 0.0
        aButton = KeptUp
        bButton = KeptUp
        xButton = KeptUp
        yButton = KeptUp
        leftBumper = KeptUp
        rightBumper = KeptUp
        leftStickButton = KeptUp
        rightStickButton = KeptUp
        startButton = KeptUp
        backButton = KeptUp
    }
}