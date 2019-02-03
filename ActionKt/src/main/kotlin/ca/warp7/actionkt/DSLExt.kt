@file:Suppress("unused")

package ca.warp7.actionkt


fun ActionDSLBase.runOnce(block: () -> Unit) = object : Action {
    override fun start() = block()
}

fun ActionDSLBase.periodic(block: () -> Unit) = object : Action {
    override fun update() = block()
    override val shouldFinish: Boolean
        get() = false
}