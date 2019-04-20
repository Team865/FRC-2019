package ca.warp7.frc.control

import ca.warp7.actionkt.Action

class OpenLoopState(private val setter: (Double) -> Unit) : Action {
    var speed: Double = 0.0
    override fun update() = setter(speed)
    override fun stop() = setter(0.0)
    override val shouldFinish: Boolean
        get() = false
}