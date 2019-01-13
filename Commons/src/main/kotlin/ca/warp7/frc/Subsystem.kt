package ca.warp7.frc

import ca.warp7.actionkt.Action

abstract class Subsystem : InputSystem() {

    internal var state: Action? = null

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
    fun <T : Action> set(newState: T, block: T.() -> Unit = {}) {
        if (!initialized) {
            initInputs()
            CommonRobot.subsystems.add(this)
        }
        if (newState !== state) {
            state?.stop()
            newState.start()
            state = newState
        }
        block.invoke(newState)
    }
}
