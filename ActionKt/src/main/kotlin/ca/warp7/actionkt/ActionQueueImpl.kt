package ca.warp7.actionkt

class ActionQueueImpl : ActionDSLImpl(), ActionQueue {

    private val queue: MutableList<NameableAction> = mutableListOf()
    var currentName: String? = null
    var currentAction: Action? = null
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
            val (action, name) = queue.removeAt(0)
            action.start()
            currentAction = action
            currentName = name
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
        for ((index, task) in queue.withIndex()) {
            print("--" + (task.name ?: task::class.java.simpleName + index))
        }
    }
}