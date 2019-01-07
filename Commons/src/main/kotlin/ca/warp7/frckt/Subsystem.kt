package ca.warp7.frckt

abstract class Subsystem : InputSystem() {

    var state: Action? = null

    var idle = true

    /**
     *
     * Called when the robot is disabled
     *
     *
     * This method should reset everything having to do with output so as to put
     * the subsystem in a disabled state
     */
    abstract fun onDisabled()


    /**
     * Called when the subsystem does not currently have a state
     */
    open fun onIdle() = onDisabled()

    /**
     *
     * Called periodically for the subsystem to send outputs to its output device.
     * This method is called from the State Change Looper.
     *
     *
     * This method is guaranteed to not be called when the robot is disabled.
     * Any output limits should be applied here for safety reasons.
     */
    abstract fun onOutput()

    fun <T : Action> setState(action: T, block: T.() -> Unit = {}) {
        state?.stop()
        action.start()
        state = action
        block.invoke(action)
    }

    @Synchronized
    fun setIdle() {
        initInputs()
        Lifecycle.subsystems.add(this)
        idle = true
    }
}
