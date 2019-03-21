package ca.warp7.actionkt

class ActionAsyncImpl : ActionDSLImpl(), ActionAsyncGroup {
    override fun Action.unaryPlus() {
        asyncActions.add(this)
    }

    private val asyncActions = mutableListOf<Action>()

    override val shouldFinish: Boolean get() = asyncActions.all { it.shouldFinish }
    override fun update() = asyncActions.forEach { it.update() }
    override fun stop() = asyncActions.forEach { it.stop() }
    override fun start() = asyncActions.forEach { it.start() }
}