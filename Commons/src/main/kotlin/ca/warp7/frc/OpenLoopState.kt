package ca.warp7.frc

import ca.warp7.actionkt.Action

class OpenLoopState(val setter: (Double) -> Unit) : Action {
    var speed: Double = 0.0
    override fun update() = setter(speed)
}