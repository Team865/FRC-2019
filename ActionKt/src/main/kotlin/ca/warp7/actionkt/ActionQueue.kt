package ca.warp7.actionkt

class ActionQueue : ActionDSL() {
    operator fun Action.unaryPlus() {

    }

    val Int.seconds get() = runOnce { }

    val Double.seconds get() = runOnce { }
}