package ca.warp7.frc

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.runOnce

fun runPeriodicLoop() = CommonRobot.pauseOnCrashPeriodicLoop()

fun disableRobot() = CommonRobot.disableOutputs()

fun RobotControlLoop.start() = CommonRobot.setLoop(this)

fun runAutonomous(mode: () -> Action, timeout: Double = 15.0): Action = CommonRobot.runAutonomous(mode, timeout)

fun Double.epsilonEquals(other: Double, epsilon: Double) = this - epsilon <= other && this + epsilon >= other

fun getShuffleboardTab(subsystem: Subsystem) = subsystem.tab

inline fun withDriver(block: RobotController.() -> Unit) = block(Controls.robotDriver)

inline fun withOperator(block: RobotController.() -> Unit) = block(Controls.robotOperator)

fun <T : Subsystem> T.set(block: T.() -> Unit) = set(runOnce(block))

fun setControllerMode(controllerMode: ControllerMode) = CommonRobot.setControllerMode(controllerMode)

fun startNotifier() = CommonRobot.startNotifier()