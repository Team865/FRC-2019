package ca.warp7.actionkt

class ActionAsyncImpl : ActionDSLImpl(), ActionAsyncGroup {
    override fun Action.unaryPlus() {
    }

    override var endOnAnyFinished: Boolean = false
}