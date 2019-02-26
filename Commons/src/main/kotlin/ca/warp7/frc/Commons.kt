package ca.warp7.frc

import ca.warp7.actionkt.ActionStateMachine
import ca.warp7.actionkt.runOnce

fun runPeriodicLoop() = CommonRobot.pauseOnCrashPeriodicLoop()

fun disableRobot() = CommonRobot.disableOutputs()

fun Double.epsilonEquals(other: Double, epsilon: Double) = this - epsilon <= other && this + epsilon >= other

fun getShuffleboardTab(subsystem: Subsystem) = subsystem.tab

inline fun withDriver(block: RobotController.() -> Unit) = block(Controls.robotDriver)

inline fun withOperator(block: RobotController.() -> Unit) = block(Controls.robotOperator)

fun <T : ActionStateMachine> T.set(block: T.() -> Unit) = set(runOnce(block))

