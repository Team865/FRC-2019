@file:Suppress("unused")

package ca.warp7.actionkt

import ca.warp7.actionj.IAction
import ca.warp7.actionj.impl.Queue

val Action.javaAction: IAction get() = JavaAction(this)
val IAction.ktAction: Action get() = KotlinAction(this)

fun actionTimer(timer: () -> Double) = IAction.ITimer { timer() }

fun javaAction(factory: IAction.API.() -> IAction) = factory(Queue()).ktAction

fun javaMode(factory: IAction.API.() -> IAction) = { javaAction(factory) }

fun action(block: ActionDSL.() -> Unit): Action = ActionDSLImpl().apply(block)

fun async(block: ActionAsyncGroup.() -> Unit): Action = ActionAsyncImpl().apply(block)

fun queue(block: ActionQueue.() -> Unit): Action = ActionQueueImpl().apply(block)

fun mode(block: ActionDSL.() -> Unit): () -> Action = { action(block) }

fun <T> T.runOnce(block: T.() -> Unit) = object : Action {
    override fun start() = block(this@runOnce)
}

fun <T> T.periodic(block: T.() -> Unit) = object : Action {
    override fun update() = block(this@periodic)
    override val shouldFinish: Boolean
        get() = false
}

fun ActionDSL.runOnce(block: () -> Unit) = object : Action {
    override fun start() = block()
}

fun ActionDSL.periodic(block: () -> Unit) = object : Action {
    override fun update() = block()
    override val shouldFinish: Boolean
        get() = false
}

fun wait(seconds: Int) = wait(seconds.toDouble())

fun wait(seconds: Double) = action { finishWhen { elapsed > seconds } }