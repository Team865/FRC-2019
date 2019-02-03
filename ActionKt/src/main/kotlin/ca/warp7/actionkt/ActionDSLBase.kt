package ca.warp7.actionkt

@ActionDSLMarker
open class ActionDSLBase : ActionDSL, Action, ActionState {
    override var elapsed: Double = 0.0

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
        get() = masterPredicate(this) || maybeFinish()

    override fun update() {
        preUpdate?.invoke()
        realUpdate()
    }

    override fun stop() {
        preStop?.invoke()
        realStop()
    }

    override fun onStart(block: () -> Unit) {
        if (preStart != null) throw IllegalStateException()
        else preStart = block
    }

    override fun finishWhen(block: ActionState.() -> Boolean) {
        masterPredicate = block
    }

    override fun onUpdate(block: () -> Unit) {
        if (preUpdate != null) throw IllegalStateException()
        else preUpdate = block
    }

    override fun onStop(block: () -> Unit) {
        if (preStop != null) throw IllegalArgumentException()
        else preStop = block
    }
}