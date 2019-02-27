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
 * input, output, init, disabled, resetting, debugging, and state updating.
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

    private fun initialize() {
        initialized = true
        CommonRobot.addSubsystem(this)
    }

    /**
     * Sets the current state of the subsystem
     */
    override fun <T : Action> set(wantedState: T, block: T.() -> Unit) {
        if (!initialized) initialize()
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

    fun get(name: String) = entries[name]

    /**
     * Put data into shuffleboard
     */
    fun put(
            name: String,
            value: Any,
            widget: BuiltInWidgets? = null,
            extras: Map<String, Any>? = null
    ) {
        if (name in entries) entries[name]?.setValue(value) else {
            val n = entries.size + sent.size
            val row = n / 5
            val col = n % 5
            val w = tab.add(name, value).withPosition(col * 7, row * 8).withSize(7, 8)
            widget?.also { w.withWidget(it) }
            extras?.also { w.withProperties(it) }
            entries[name] = w.entry
        }
    }

    fun graph(name: String, value: Any) {
        put(name, value, BuiltInWidgets.kGraph)
    }

    /**
     * Put data into shuffleboard
     */
    fun putIfNonEmpty(
            name: String,
            value: Any,
            widget: BuiltInWidgets? = null,
            extras: Map<String, Any>? = null
    ) {
        if (entries.isNotEmpty()) {
            val n = entries.size + sent.size
            val row = n / 5
            val col = n % 5
            val w = tab.add(name, value).withPosition(col * 7, row * 8).withSize(7, 8)
            widget?.also { w.withWidget(it) }
            extras?.also { w.withProperties(it) }
            entries[name] = w.entry
        }
    }

    private val sent: MutableList<String> = mutableListOf()

    /**
     * Put data into shuffleboard
     */
    fun put(
            value: Sendable,
            widget: BuiltInWidgets? = null,
            extras: Map<String, String>? = null
    ) {
        val name = value.name
        if (name !in sent) {
            val n = entries.size + sent.size
            val row = n / 5
            val col = n % 5
            sent.add(name)
            val w = tab.add(name, value).withPosition(col * 7, row * 8).withSize(7, 8)
            widget?.also { w.withWidget(it) }
            extras?.also { w.withProperties(it) }
        }
    }
}
