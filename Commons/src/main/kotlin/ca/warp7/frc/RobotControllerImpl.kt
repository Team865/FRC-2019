package ca.warp7.frc

class RobotControllerImpl internal constructor() : RobotController {
    var backButton = ControllerState.KeptUp
    override var aButton = ControllerState.KeptUp
    override var bButton = ControllerState.KeptUp
    override var xButton = ControllerState.KeptUp
    override var yButton = ControllerState.KeptUp
    override var leftBumper = ControllerState.KeptUp
    override var rightBumper = ControllerState.KeptUp
    override var leftStickButton = ControllerState.KeptUp
    override var rightStickButton = ControllerState.KeptUp
    override var startButton = ControllerState.KeptUp
    override var leftTriggerAxis = 0.0
    override var rightTriggerAxis = 0.0
    override var leftXAxis = 0.0
    override var leftYAxis = 0.0
    override var rightXAxis = 0.0
    override var rightYAxis = 0.0
}

