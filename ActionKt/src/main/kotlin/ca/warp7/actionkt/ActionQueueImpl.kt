package ca.warp7.actionkt

class ActionQueueImpl : ActionDSLImpl(), ActionQueue {

    private val queue: MutableList<NameableAction> = mutableListOf()
    var currentAction: NameableAction? = null
    var started = false

    override operator fun Action.unaryPlus() {
        if (!started) queue.add(NameableAction(this))
    }

    override fun start() {
        super.start()
        started = true
    }

    override val shouldFinish: Boolean
        get() = super.shouldFinish || queue.isEmpty()

    override fun update() {
        super.update()
        if (currentAction == null) {
            if (queue.isEmpty()) return
            currentAction = queue.removeAt(0)
            currentAction?.action?.start()
        }
        currentAction?.action?.update()
        if (currentAction?.action?.shouldFinish == true) {
            currentAction?.action?.stop()
            currentAction = null
        }
    }

    override fun stop() {
        currentAction?.action?.stop()
        super.stop()
    }
}