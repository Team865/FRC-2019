package ca.warp7.frc2019

import ca.warp7.actionkt.Action

@Suppress("unused")
object MainLoopAction : Action {
    override fun start() {
        MainLoop.setup()
    }

    override val shouldFinish = false

    override fun update() {
        MainLoop.periodic()
    }
}