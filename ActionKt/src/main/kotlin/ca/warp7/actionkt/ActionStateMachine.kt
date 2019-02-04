package ca.warp7.actionkt

interface ActionStateMachine {
    fun <T : Action> set(wantedState: T, block: T.() -> Unit = {})
}