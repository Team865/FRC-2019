@file:Suppress("unused")

package ca.warp7.actionkt


fun action(block: ActionDSL.() -> Unit): Action = ActionDSLImpl().apply(block)

fun queue(block: ActionQueue.() -> Unit): Action = ActionQueueImpl().apply(block)

fun mode(block: ActionDSL.() -> Unit): () -> Action = { action(block) }