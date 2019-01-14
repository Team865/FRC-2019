package ca.warp7.frc

import ca.warp7.actionkt.Action
import edu.wpi.first.wpilibj.RobotBase

fun runMainLoop() = CommonRobot.mainLoop()

fun disableRobot() = CommonRobot.disableOutputs()

fun setControlLoop(controlLoop: RobotControlLoop?) {
    CommonRobot.controlLoop = controlLoop
}

fun runAutonomous(mode: () -> Action, timeout: Double = 15.0): Action = CommonRobot.runAutonomous(mode, timeout)

fun limit(value: Double, lim: Double): Double = Math.max(-1 * Math.abs(lim), Math.min(value, Math.abs(lim)))

fun <T : RobotBase> startRobot(robot: T) = RobotBase.startRobot { robot }

private fun controller(port: Int) = RobotController(port).also { CommonRobot.controllers.add(it) }

object Controls {
    val driver = controller(0)
    val operator = controller(1)
}