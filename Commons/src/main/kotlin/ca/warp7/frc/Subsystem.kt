package ca.warp7.frc

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.ActionStateMachine
import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.wpilibj.Sendable
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab

/**
 * Subsystem defines a robot subsystem. A good example of a subsystem is the drive train or
 * a claw. ISubsystem defines many important callbacks for making a subsystem.
 *
 * All motors should be a part of a subsystem. For instance, all the wheel motors should be a
 * part of some kind of "drive train" subsystem. Every motor should also be only in one subsystem,
 * they should also not be used by multiple subsystems.
 *
 * All sensor devices such as encoders and cameras should be in the subsystem where their
 * measured values have the most direct impact on the motors and/or other output devices of
 * the said subsystem, except in the case of classes extending {@link IComponent}, in which
 * components can be used by multiple subsystems.
 *
 * Each subsystem should have only one instance. They should use objects in Kotlin.
 *
 * This interface defines all the callbacks a subsystem should have, including handlers for
 * input, output, init, disabled, resetting, debugging, and state updating. It is managed by
 * {@link Components} and called periodically by the {@link LoopsManager} class during
 * different phases of robot runtime
 *
 * All the methods except onConstruct are empty default methods. Choose the appropriate one to
 * implement. They will be called properly by {@link Components} regardless which
 * methods are implemented
 *
 * A good implementation strategy is define specific object classes that holds the input state
 * and current state of the subsystem respectively. This interface defines some annotations
 * markers in order to make it clear about usage of this strategy
 *
 * Finally, it is very important that implementations of these methods are <b>synchronized</b>
 * because they are most often called from different threads. It's also important
 * that the periodic functions are not blocking operations as to prevent leaking.
 */

abstract class Subsystem : ActionStateMachine() {

    /**
     * Called when the robot is disabled
     *
     *
     * This method should reset everything having to do with output so as to put
     * the subsystem in a disabled state
     */
    open fun onDisabled() {}

    /**
     * Called periodically for the subsystem to send outputs to its output device.
     *
     * This method is guaranteed to not be called when the robot is disabled.
     * Any output limits should be applied here for safety reasons.
     */
    open fun onOutput() {}

    private var initialized = false

    /**
     * Sets the current state of the subsystem
     */
    override fun <T : Action> set(wantedState: T, block: T.() -> Unit) {
        if (!initialized) {
            initialized = true
            CommonRobot.subsystems.add(this)
        }
        super.set(wantedState, block)
    }

    override fun updateState() {
        super.updateState()
        onOutput()
    }

    internal val tab: ShuffleboardTab by lazy { Shuffleboard.getTab(this::class.java.simpleName) }

    /**
     *
     * Called periodically for the subsystem to get measurements from its input devices.
     *
     * Note that this method may still be called while the robot is disabled, so
     * extra care should be made that it performs no outputting
     */
    open fun onMeasure(dt: Double) {}

    /**
     * Send values to shuffleboard
     */
    open fun onPostUpdate() {}

    private val entries: MutableMap<String, NetworkTableEntry> = mutableMapOf()

    protected fun get(name: String) = entries[name]

    /**
     * Put data into shuffleboard
     */
    protected fun put(
            name: String,
            value: Any,
            x: Int = 0,
            y: Int = 0,
            width: Int = 0,
            height: Int = 0,
            widget: BuiltInWidgets? = null,
            extras: Map<String, Any>? = null
    ) {
        if (name in entries) entries[name]?.setValue(value) else {
            val w = tab.add(name, value).withPosition(x, y).withSize(width, height)
            widget?.also { w.withWidget(it) }
            extras?.also { w.withProperties(it) }
            entries[name] = w.entry
        }
    }

    private val sent: MutableList<String> = mutableListOf()

    /**
     * Put data into shuffleboard
     */
    protected fun put(
            value: Sendable,
            x: Int = 0,
            y: Int = 0,
            width: Int = 0,
            height: Int = 0,
            widget: BuiltInWidgets? = null,
            extras: Map<String, String>? = null
    ) {
        val name = value.name
        if (name !in sent) {
            sent.add(name)
            val w = tab.add(name, value).withPosition(x, y).withSize(width, height)
            widget?.also { w.withWidget(it) }
            extras?.also { w.withProperties(it) }
        }
    }
}
