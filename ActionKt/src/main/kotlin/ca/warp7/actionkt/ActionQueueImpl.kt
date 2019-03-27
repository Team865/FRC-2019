package ca.warp7.actionkt

class ActionQueueImpl : ActionDSLImpl(), ActionQueue {

    private val queue: MutableList<Action> = mutableListOf()
    private var currentAction: Action? = null
    private var started = false

    init {
        finishWhen { queue.isEmpty() && currentAction == null }
    }

    override operator fun Action.unaryPlus() {
        if (!started) queue.add(this)
    }

    override fun start() {
        super.start()
        started = true
    }

    override fun update() {
        super.update()
        if (currentAction == null) {
            if (queue.isEmpty()) return
            val action = queue.removeAt(0)
            action.start()
            currentAction = action
        }
        currentAction?.update()
        if (currentAction?.shouldFinish != false) {
            currentAction?.stop()
            currentAction = null
        }
    }

    override fun stop() {
        currentAction?.stop()
        super.stop()
    }

    override fun printTaskGraph() {
    }
}