package ca.warp7.actionkt

@ActionDSLMarker
interface ActionDSL {
    fun onStart(block: ActionState.() -> Unit)
    fun finishWhen(block: ActionState.() -> Boolean)
    fun onUpdate(block: ActionState.() -> Unit)
    fun onStop(block: ActionState.() -> Unit)
}