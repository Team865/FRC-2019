package ca.warp7.actionkt

import ca.warp7.actionj.IAction

internal class KotlinAction(val action: IAction) : Action {
    override fun start() = action.start()
    override val shouldFinish: Boolean get() = action.shouldFinish()
    override fun update() = action.update()
    override fun stop() = action.stop()
}