package ca.warp7.actionkt

import ca.warp7.actionj.IAction

internal class JavaAction(val action: Action) : IAction {
    override fun start() = action.start()
    override fun shouldFinish(): Boolean = action.shouldFinish
    override fun update() = action.update()
    override fun stop() = action.stop()
}