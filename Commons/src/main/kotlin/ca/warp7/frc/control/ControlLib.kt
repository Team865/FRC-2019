package ca.warp7.frc.control

import ca.warp7.actionkt.ActionStateMachine
import ca.warp7.actionkt.future
import ca.warp7.actionkt.runOnce
import edu.wpi.first.wpilibj.Timer

inline fun withDriver(block: RobotController.() -> Unit) = block(Controls.robotDriver)

inline fun withOperator(block: RobotController.() -> Unit) = block(Controls.robotOperator)

fun <T : ActionStateMachine> T.set(block: T.() -> Unit) = set(runOnce(block))

fun <T : ActionStateMachine> T.future(block: T.() -> Unit) = future(runOnce(block))

fun runPeriodicLoop() {
    val time = Timer.getFPGATimestamp()
    InternalControl.haltingPeriodicLoop()
    val loopTime = Timer.getFPGATimestamp() - time
    if (loopTime > 0.02) {
        println("Slow loop time$loopTime")
    }
}

fun disableRobot() = InternalControl.disableOutputs()