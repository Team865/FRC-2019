@file:Suppress("unused")

package ca.warp7.actionkt


fun action(block: ActionDSL.() -> Unit): Action = ActionDSLBase().apply(block)

fun queue(block: ActionQueue.() -> Unit): Action = ActionQueue().apply(block)

fun mode(block: ActionDSL.() -> Unit): () -> Action = { action(block) }