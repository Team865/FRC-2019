package ca.warp7.actionkt

import ca.warp7.actionj.IAction
import ca.warp7.actionj.impl.Queue

private class JavaAction(val action: Action) : IAction {
    override fun start() = action.start()
    override fun shouldFinish(): Boolean = action.shouldFinish
    override fun update() = action.update()
    override fun stop() = action.stop()
}

private class KotlinAction(val action: IAction) : Action {
    override fun start() = action.start()
    override val shouldFinish: Boolean get() = action.shouldFinish()
    override fun update() = action.update()
    override fun stop() = action.stop()
}

val Action.javaAction: IAction get() = JavaAction(this)
val IAction.ktAction: Action get() = KotlinAction(this)


fun actionTimer(timer: () -> Double) = IAction.ITimer { timer() }

fun javaAction(factory: IAction.API.() -> IAction) = factory(Queue()).ktAction

fun javaMode(factory: IAction.API.() -> IAction) = { javaAction(factory) }