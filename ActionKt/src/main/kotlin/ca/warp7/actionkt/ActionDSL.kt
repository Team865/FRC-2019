package ca.warp7.actionkt

@ActionDSLMarker
interface ActionDSL {
    fun onStart(block: () -> Unit)
    fun finishWhen(block: ActionState.() -> Boolean)
    fun onUpdate(block: () -> Unit)
    fun onStop(block: () -> Unit)
}