package ca.warp7.actionkt

class ASMPeriodic<T : ActionStateMachine>(private val value: T, private val block: T.() -> Unit) : Action {
    override fun update() = block(value)
    override val shouldFinish: Boolean get() = false
}