package ca.warp7.frc.control

interface RobotController {
    val aButton: ControllerState
    val bButton: ControllerState
    val xButton: ControllerState
    val yButton: ControllerState
    val leftBumper: ControllerState
    val rightBumper: ControllerState
    val leftStickButton: ControllerState
    val rightStickButton: ControllerState
    val backButton: ControllerState
    val startButton: ControllerState
    val leftTriggerAxis: Double
    val rightTriggerAxis: Double
    val leftXAxis: Double
    val leftYAxis: Double
    val rightXAxis: Double
    val rightYAxis: Double
}