package ca.warp7.frc

import edu.wpi.first.wpilibj.XboxController

@Suppress("unused")
class RobotController internal constructor(internal val port: Int) {

    internal val data = ControllerData()
    internal val controller = XboxController(port)

    val aButton get() = data.aButton
    val bButton get() = data.bButton
    val xButton get() = data.xButton
    val yButton get() = data.yButton
    val leftBumper get() = data.leftBumper
    val rightBumper get() = data.rightBumper
    val leftStickButton get() = data.leftStickButton
    val rightStickButton get() = data.rightStickButton
    val startButton get() = data.startButton
    val backButton get() = data.backButton
    val leftTriggerAxis get() = data.leftTriggerAxis
    val rightTriggerAxis get() = data.rightTriggerAxis
    val leftXAxis get() = data.leftXAxis
    val leftYAxis get() = data.leftYAxis
    val rightXAxis get() = data.rightXAxis
    val rightYAxis get() = data.rightYAxis
    var controllerEnabled = false

    override fun equals(other: Any?): Boolean {
        return other is RobotController && port == other.port
    }

    override fun hashCode(): Int {
        return port
    }
}

