package ca.warp7.actionkt

@ActionDSLMarker
open class ActionDSL : Action, ActionState {
    override val elapsed: Double
        get() = TODO("not implemented")

    private var preStart: (() -> Unit)? = null
    private var preUpdate: (() -> Unit)? = null
    private var preStop: (() -> Unit)? = null
    private var masterPredicate: ActionState.() -> Boolean = { true }

    open fun realStart() = Unit
    open fun realUpdate() = Unit
    open fun maybeFinish() = true
    open fun realStop() = Unit

    override fun start() {
        preStart?.invoke()
        realStart()
    }

    override val shouldFinish: Boolean
        get() = masterPredicate(SimpleActionState()) || maybeFinish()

    override fun update() {
        preUpdate?.invoke()
        realUpdate()
    }

    override fun stop() {
        preStop?.invoke()
        realStop()
    }

    fun onStart(block: () -> Unit) {
        if (preStart != null) throw IllegalStateException()
        else preStart = block
    }

    fun finishWhen(block: ActionState.() -> Boolean) {
        masterPredicate = block
    }

    fun onUpdate(block: () -> Unit) {
        if (preUpdate != null) throw IllegalStateException()
        else preUpdate = block
    }

    fun onStop(block: () -> Unit) {
        if (preStop != null) throw IllegalArgumentException()
        else preStop = block
    }
}