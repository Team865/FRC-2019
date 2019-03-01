package ca.warp7.actionkt

abstract class ActionStateMachine {

    internal var currentState: Action? = null
        private set

    /**
     * Sets the state machine to a wanted state
     */
    open fun <T : Action> set(wantedState: T, block: T.() -> Unit = {}) {
        block(wantedState)
        // Check if there is a new wanted state that is not the same as the current state
        if (wantedState != currentState) {
            // stop the current state
            stopState()
            // Start the new state
            wantedState.start()
            // Change to the new state
            currentState = wantedState
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
    open fun stopState() {
        val unlockedState = currentState
        // first set to no state
        currentState = null
        // now stop. There shouldn't be a recursive issue now
        unlockedState?.stop()
    }
}