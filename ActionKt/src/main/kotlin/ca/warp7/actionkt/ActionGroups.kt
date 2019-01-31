@file:Suppress("unused")

package ca.warp7.actionkt


fun action(block: ActionDSL.() -> Unit) = ActionDSL().apply(block).toAction()

fun queue(block: ActionQueue.() -> Unit) = ActionQueue().apply(block).toAction()

fun mode(block: ActionDSL.() -> Unit) = { action(block) }