package ca.warp7.actionkt

interface ActionDSL {
    fun onStart(block: () -> Unit)
    fun finishWhen(block: ActionState.() -> Boolean)
    fun onUpdate(block: () -> Unit)
    fun onStop(block: () -> Unit)
}