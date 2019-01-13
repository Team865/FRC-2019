package ca.warp7.actionkt

import ca.warp7.actionj.IAction

private class JavaAction(val action: Action) : IAction {
    override fun start() = action.start()
    override fun shouldFinish(): Boolean = action.shouldFinish()
    override fun update() = action.update()
    override fun stop() = action.stop()
}

private class KotlinAction(val action: IAction) : Action {
    override fun start() = action.start()
    override fun shouldFinish(): Boolean = action.shouldFinish()
    override fun update() = action.update()
    override fun stop() = action.stop()
}

val Action.javaAction: IAction get() = JavaAction(this)
val IAction.ktAction: Action get() = KotlinAction(this)

class NothingAction : Action

private class RunOnce(private val block: () -> Unit)

fun runOnce(block: () -> Unit): Action = object : Action {
    override fun start() = block.invoke()
}