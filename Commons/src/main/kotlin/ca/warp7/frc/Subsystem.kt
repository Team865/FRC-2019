package ca.warp7.frc

import ca.warp7.actionkt.Action

abstract class Subsystem : InputSystem() {

    internal var currentState: Action? = null
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
    fun <T : Action> set(wantedState: T, block: T.() -> Unit = {}) {
        if (!initialized) {
            initialized = true
            initInputs()
            CommonRobot.subsystems.add(this)
        }
        // Check if there is a new wanted state that is not the same as the current state
        if (wantedState != currentState) {
            // Change to the new state
            currentState = wantedState
            // Start the new state
            currentState?.start()
        }
        block.invoke(wantedState)
    }
}
