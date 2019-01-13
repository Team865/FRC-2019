package ca.warp7.frc

import ca.warp7.actionkt.Action

abstract class Subsystem : InputSystem() {

    internal var currentState: Action? = null
    internal var wantedState: Action? = null

    private var initialized = false

    /**
     * Called when the robot is disabled
     *
     *
     * This method should reset everything having to do with output so as to put
     * the subsystem in a disabled state
     */
    abstract fun onDisabled()

    /**
     * Called periodically for the subsystem to send outputs to its output device.
     *
     * This method is guaranteed to not be called when the robot is disabled.
     * Any output limits should be applied here for safety reasons.
     */
    abstract fun onOutput()

    /**
     * Sets the current state of the subsystem
     */
    fun <T : Action> set(state: T, block: T.() -> Unit = {}) {
        if (!initialized) {
            initialized = true
            initInputs()
            CommonRobot.subsystems.add(this)
        }
        wantedState = state
        block.invoke(state)
    }
}
