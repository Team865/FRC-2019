package ca.warp7.frc

class RobotControllerImpl internal constructor() : RobotController {
    var backButton = ControllerState.None
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
}

