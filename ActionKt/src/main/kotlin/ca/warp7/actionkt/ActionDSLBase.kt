package ca.warp7.actionkt

@ActionDSLMarker
open class ActionDSLBase : ActionDSL, Action, ActionState {
    override var elapsed: Double = 0.0

    private var start: (() -> Unit)? = null
    private var update: (() -> Unit)? = null
    private var stop: (() -> Unit)? = null
    private var predicate: ActionState.() -> Boolean = { true }

    override fun start() {
        start?.invoke()
    }

    override val shouldFinish: Boolean
        get() = predicate(this)

    override fun update() {
        update?.invoke()
    }

    override fun stop() {
        stop?.invoke()
    }

    override fun onStart(block: () -> Unit) {
        if (start != null) throw IllegalStateException()
        else start = block
    }

    override fun finishWhen(block: ActionState.() -> Boolean) {
        predicate = block
    }

    override fun onUpdate(block: () -> Unit) {
        if (update != null) throw IllegalStateException()
        else update = block
    }

    override fun onStop(block: () -> Unit) {
        if (stop != null) throw IllegalArgumentException()
        else stop = block
    }
}