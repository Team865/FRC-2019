package ca.warp7.frckt

class ControllerData {
    val aButton get() = ControllerState.KeptUp
    val bButton get() = ControllerState.KeptUp
    val xButton get() = ControllerState.KeptUp
    val yButton get() = ControllerState.KeptUp
    val leftBumper get() = ControllerState.KeptUp
    val rightBumper get() = ControllerState.KeptUp
    val leftStickButton get() = ControllerState.KeptUp
    val rightStickButton get() = ControllerState.KeptUp
    val startButton get() = ControllerState.KeptUp
    val backButton get() = ControllerState.KeptUp

    val leftTriggerAxis get() = 0.0
    val rightTriggerAxis get() = 0.0
    val leftXAxis get() = 0.0
    val leftYAxis get() = 0.0
    val rightXAxis get() = 0.0
    val rightYAxis get() = 0.0
}