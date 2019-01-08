package ca.warp7.frc

import ca.warp7.actionkt.Action

fun runMainLoop() = CommonsRobot.mainLoop()

fun disableRobot() = CommonsRobot.disableOutputs()

fun setControlLoop(controlLoop: ControlLoop?) {
    CommonsRobot.controlLoop = controlLoop
}

fun runAutonomous(mode: () -> Action, timeout: Double = 15.0): Action = CommonsRobot.runAutonomous(mode, timeout)

fun limit(value: Double, lim: Double): Double = Math.max(-1 * Math.abs(lim), Math.min(value, Math.abs(lim)))

val driver = RobotController(0).also { CommonsRobot.controllers.add(it) }
val operator = RobotController(1).also { CommonsRobot.controllers.add(it) }