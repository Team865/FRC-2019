@file:Suppress("unused")

package ca.warp7.actionkt

import ca.warp7.actionj.IAction
import ca.warp7.actionj.impl.Queue

private typealias ASM = ActionStateMachine

val Action.javaAction: IAction get() = JavaAction(this)

val IAction.ktAction: Action get() = KotlinAction(this)

fun actionTimer(timer: () -> Double) = IAction.ITimer { timer() }

fun javaAction(factory: IAction.API.() -> IAction) = factory(Queue()).ktAction

fun javaMode(factory: IAction.API.() -> IAction) = { javaAction(factory) }

fun action(block: ActionDSL.() -> Unit): Action = ActionDSLImpl().apply(block)

fun async(block: ActionAsyncGroup.() -> Unit): Action = ActionAsyncImpl().apply(block)

fun queue(block: ActionQueue.() -> Unit): Action = ActionQueueImpl().apply(block)

fun mode(block: ActionDSL.() -> Unit): () -> Action = { action(block) }

fun series(block: ActionQueue.() -> Unit): Action = ActionQueueImpl().apply(block)

fun parallel(block: ActionAsyncGroup.() -> Unit): Action = ActionAsyncImpl().apply(block)

fun await(action: Action) = action

fun waitUntil(predicate: ActionState.() -> Boolean) = action { finishWhen(predicate) }

fun wait(seconds: Int) = wait(seconds.toDouble())

fun wait(seconds: Double) = waitUntil { elapsed > seconds }

fun cleanup(block: ActionState.() -> Unit) = action { onStop(block) }

inline fun <T : ActionStateMachine> T.runOnce(crossinline block: T.() -> Unit) = object : Action {
    override fun start() = block(this@runOnce)
    override val shouldFinish: Boolean get() = false
}

inline fun <T : ActionStateMachine> T.periodic(crossinline block: T.() -> Unit) = object : Action {
    override fun update() = block(this@periodic)
    override val shouldFinish: Boolean get() = false
}

fun ActionDSL.runOnce(block: ActionState.() -> Unit) = action {
    onStart(block)
    finishWhen { false }
}

fun ActionDSL.periodic(block: ActionState.() -> Unit) = action {
    onUpdate(block)
    finishWhen { false }
}

inline fun runOnce(crossinline block: () -> Unit) = object : Action {
    override fun start() = block()
    override val shouldFinish: Boolean get() = false
}

inline fun periodic(crossinline block: () -> Unit) = object : Action {
    override fun update() = block()
    override val shouldFinish: Boolean get() = false
}

fun <T : Action> ASM.future(wantedState: T, block: T.() -> Unit = {}) = runOnce { set(wantedState, block) }