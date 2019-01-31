package ca.warp7.frc

import ca.warp7.actionkt.Action

fun runPeriodicLoop() = CommonRobot.pauseOnCrashPeriodicLoop()

fun disableRobot() = CommonRobot.disableOutputs()

fun setLoop(loop: RobotControlLoop?) {
    CommonRobot.controlLoop = loop
}

fun runAutonomous(mode: () -> Action, timeout: Double = 15.0): Action = CommonRobot.runAutonomous(mode, timeout)

fun limit(value: Double, lim: Double): Double = Math.max(-1 * Math.abs(lim), Math.min(value, Math.abs(lim)))

object Controls {
    val robotDriver = CommonRobot.robotDriver
    val robotOperator = CommonRobot.robotOperator
}

