@file:Suppress("unused")

package ca.warp7.actionkt

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

fun <T : ActionStateMachine> T.runOnce(block: T.() -> Unit) = ASMRunOnce(this, block)

fun <T : ActionStateMachine> T.periodic(block: T.() -> Unit) = ASMPeriodic(this, block)

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

fun runAfter(seconds: Int, block: ActionState.() -> Unit) = runAfter(seconds.toDouble(), block)

fun runAfter(seconds: Double, block: ActionState.() -> Unit) = action {
    finishWhen { elapsed > seconds }
    onStop(block)
}

fun <T : Action> ActionStateMachine.future(wantedState: T, block: T.() -> Unit = {}) =
        runOnce { set(wantedState, block) }

/**
 * Tries to set the state machine to a wanted state if the current state can finish
 */
fun <T : Action> ActionStateMachine.trySet(wantedState: T, block: T.() -> Unit = {}) {
    val currentState = currentState
    if (wantedState == currentState) {
        block(wantedState)
    } else if (currentState == null || currentState.shouldFinish) {
        set(wantedState, block)
    }
}