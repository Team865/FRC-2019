package ca.warp7.actionkt

interface ActionAsyncGroup : ActionDSL {
    operator fun Action.unaryPlus()
}