@file:Suppress("unused")

package ca.warp7.actionkt

import ca.warp7.actionj.IAction
import ca.warp7.actionj.impl.Queue


fun <T> T.runOnce(block: T.() -> Unit) = object : Action {
    override fun start() = block(this@runOnce)
}

fun <T> T.periodic(block: T.() -> Unit) = object : Action {
    override fun update() = block(this@periodic)
}

fun runOnce(block: () -> Unit): Action = object : Action {
    override fun start() = block()
}

fun periodic(block: () -> Unit): Action = object : Action {
    override fun update() = block()
}

fun javaAction(factory: IAction.API.() -> IAction) = factory(Queue())

fun javaMode(factory: IAction.API.() -> IAction) = { javaAction(factory) }