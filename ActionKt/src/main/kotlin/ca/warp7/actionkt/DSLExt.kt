@file:Suppress("unused")

package ca.warp7.actionkt


fun ActionDSLImpl.runOnce(block: () -> Unit) = object : Action {
    override fun start() = block()
}

fun ActionDSLImpl.periodic(block: () -> Unit) = object : Action {
    override fun update() = block()
    override val shouldFinish: Boolean
        get() = false
}