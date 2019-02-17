package ca.warp7.actionkt

abstract class ActionStateMachine {

    private var currentState: Action? = null

    var stateName: String = "None"
        internal set

    /**
     * Sets the state machine to a wanted state
     */
    open fun <T : Action> set(wantedState: T, block: T.() -> Unit = {}) {
        block(wantedState)
        // Check if there is a new wanted state that is not the same as the current state
        if (wantedState != currentState) {
            // stop the current state
            currentState?.stop()
            // Change to the new state
            currentState = wantedState
            // get the name of the state
            stateName = wantedState::class.java.simpleName
            // Start the new state
            currentState?.start()
        }
    }

    /**
     * Tries to set the state machine to a wanted state if the current state can finish
     */
    open fun <T : Action> trySet(wantedState: T, block: T.() -> Unit = {}) {
        val currentState = currentState
        if (wantedState != currentState && (currentState == null || currentState.shouldFinish)) {
            set(wantedState, block)
        }
    }

    /**
     * Update the current state
     */
    open fun updateState() {
        // Check if the current state wants to finish before updating
        if (currentState?.shouldFinish != false) {
            // Stop and remove the current state
            stopState()
        } else {
            // Update the current state
            currentState?.update()
        }
    }

    /**
     * Stop the current state
     */
    fun stopState() {
        currentState?.stop()
        currentState = null
    }
}