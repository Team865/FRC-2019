package ca.warp7.actionkt

@ActionDSLMarker
open class ActionDSLImpl : ActionDSL, Action, ActionState {
    override var elapsed: Double = 0.0

    private var start: ActionState.() -> Unit = {}
    private var update: ActionState.() -> Unit = {}
    private var stop: ActionState.() -> Unit = {}
    private var predicate: ActionState.() -> Boolean = { true }

    override fun start() {
        start.invoke(this)
    }

    override val shouldFinish: Boolean
        get() = predicate(this)

    override fun update() {
        update.invoke(this)
    }

    override fun stop() {
        stop.invoke(this)
    }

    override fun onStart(block: ActionState.() -> Unit) {
        start = block
    }

    override fun finishWhen(block: ActionState.() -> Boolean) {
        predicate = block
    }

    override fun onUpdate(block: ActionState.() -> Unit) {
        update = block
    }

    override fun onStop(block: ActionState.() -> Unit) {
        stop = block
    }
}