package ca.warp7.frc

import ca.warp7.actionkt.ActionStateMachine
import ca.warp7.actionkt.runOnce
import edu.wpi.first.wpilibj.Timer

fun runPeriodicLoop() {
    val time = Timer.getFPGATimestamp()
    InternalControl.haltingPeriodicLoop()
    val loopTime = Timer.getFPGATimestamp() - time
    if (loopTime > 0.02){
        println("Slow loop time$loopTime")
    }
}

fun disableRobot() = InternalControl.disableOutputs()

fun Double.epsilonEquals(other: Double, epsilon: Double) = this - epsilon <= other && this + epsilon >= other

fun Double.epsilonEquals(other: Double) = epsilonEquals(other, 1E-12)

fun getShuffleboardTab(subsystem: Subsystem) = subsystem.tab

inline fun withDriver(block: RobotController.() -> Unit) = block(Controls.robotDriver)

inline fun withOperator(block: RobotController.() -> Unit) = block(Controls.robotOperator)

fun <T : ActionStateMachine> T.set(block: T.() -> Unit) = set(runOnce(block))

fun feetToMeters(feet: Double) = feet * 0.3048

fun inchesToMeters(inches: Double) = inches * 0.0254

fun interpolate(a: Double, b: Double, x: Double) = a + (b - a) * x.coerceIn(0.0, 1.0)

val Double.f get() = "%.4f".format(this)