package ca.warp7.actionkt

abstract class ActionStateMachine {

    private var currentState: Action? = null

    var stateName: String = "None"

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

    open fun updateState() {
        // Check if the current state wants to finish before updating
        if (currentState?.shouldFinish != false) {
            // Stop and remove the current state
            currentState?.stop()
            currentState = null
        } else {
            // Update the current state
            currentState?.update()
        }
    }
}