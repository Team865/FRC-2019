package ca.warp7.actionkt

class RunOnce<T : ActionStateMachine>(private val value: T, private val block: T.() -> Unit) : Action {
    override fun start() = block(value)
    override val shouldFinish: Boolean get() = false
}