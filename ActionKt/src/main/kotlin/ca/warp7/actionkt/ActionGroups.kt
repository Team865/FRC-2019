@file:Suppress("unused")

package ca.warp7.actionkt


fun action(block: ActionDSL.() -> Unit) = ActionDSL().apply(block)

fun queue(block: ActionQueue.() -> Unit) = ActionQueue().apply(block)

fun mode(block: ActionDSL.() -> Unit) = { action(block) }