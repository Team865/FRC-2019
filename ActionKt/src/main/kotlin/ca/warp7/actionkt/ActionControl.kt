package ca.warp7.actionkt

class ActionControl : Action {
    private var currentAction: Action? = null
    private var stopping = false

    fun setAction(action: Action) {
        currentAction = action
        stopping = false
    }

    override fun start() {
        currentAction?.start()
    }

    override fun update() {
        currentAction?.update()
    }

    override fun stop() {
        currentAction?.stop()
        stopping = true
    }

    override val shouldFinish: Boolean
        get() = stopping || currentAction?.shouldFinish ?: true

    fun flagAsDone() {
        stopping = true
    }
}