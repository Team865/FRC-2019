package ca.warp7.frc

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

internal class ExecutionAction(private val exec: () -> Unit) : Action {
    override fun start() = exec.invoke()
}

val Action.javaAction: IAction get() = JavaAction(this)
val IAction.ktAction: Action get() = KotlinAction(this)

class NothingAction : Action