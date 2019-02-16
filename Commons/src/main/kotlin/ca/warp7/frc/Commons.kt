package ca.warp7.frc

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.runOnce

fun runPeriodicLoop() = CommonRobot.pauseOnCrashPeriodicLoop()

fun disableRobot() = CommonRobot.disableOutputs()

fun RobotControlLoop.start() {
    CommonRobot.controlLoop = this
}

fun runAutonomous(mode: () -> Action, timeout: Double = 15.0): Action = CommonRobot.runAutonomous(mode, timeout)

fun Double.epsilonEquals(other: Double, epsilon: Double) = this - epsilon <= other && this + epsilon >= other

fun getShuffleboardTab(subsystem: Subsystem) = subsystem.tab

fun withDriver(block: RobotController.() -> Unit) = block(CommonRobot.robotDriver)

fun withOperator(block: RobotController.() -> Unit) = block(CommonRobot.robotOperator)

fun setControllerMode(controllerMode: ControllerMode) {
    CommonRobot.controllerMode = controllerMode.value
}

inline fun <T : Subsystem> T.set(crossinline block: T.() -> Unit) = set(runOnce(block))