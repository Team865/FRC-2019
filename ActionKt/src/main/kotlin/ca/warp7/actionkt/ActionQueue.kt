package ca.warp7.actionkt

interface ActionQueue : ActionDSL {
    operator fun Action.unaryPlus()
}