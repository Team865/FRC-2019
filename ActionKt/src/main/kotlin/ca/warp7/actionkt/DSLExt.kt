@file:Suppress("unused")

package ca.warp7.actionkt


fun ActionDSL.runOnce(block: () -> Unit) = object : Action {
    override fun start() = block()
}

fun ActionDSL.periodic(block: () -> Unit) = object : Action {
    override fun update() = block()
    override val shouldFinish: Boolean
        get() = false
}