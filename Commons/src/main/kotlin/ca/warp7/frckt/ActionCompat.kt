package ca.warp7.frckt

import ca.warp7.action.IAction

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
val IAction.kotlinAction: Action get() = KotlinAction(this)