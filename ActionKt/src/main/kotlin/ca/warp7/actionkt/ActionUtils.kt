@file:Suppress("unused")

package ca.warp7.actionkt


fun <T> T.runOnce(block: T.() -> Unit) = object : Action {
    override fun start() = block(this@runOnce)
}

fun <T> T.periodic(block: T.() -> Unit) = object : Action {
    override fun update() = block(this@periodic)
}

fun action(block: ActionDSL.() -> Unit) = ActionDSL().apply(block).toAction()

fun queue(block: ActionQueue.() -> Unit) = ActionQueue().apply(block).toAction()

fun mode(block: ActionDSL.() -> Unit) = { action(block) }