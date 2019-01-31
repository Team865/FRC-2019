package ca.warp7.actionkt

open class ActionDSL {

    private var preStart: (() -> Unit)? = null
    private var preUpdate: (() -> Unit)? = null
    private var preStop: (() -> Unit)? = null
    private var masterPredicate: () -> Boolean = { true }

    open fun realStart() = Unit
    open fun realUpdate() = Unit
    open fun maybeFinish() = true
    open fun realStop() = Unit

    fun toAction() = object : Action {
        override val shouldFinish get() = masterPredicate.invoke() || maybeFinish()
        override fun start() {
            preStart?.invoke()
            realStart()
        }

        override fun update() {
            preUpdate?.invoke()
            realUpdate()
        }

        override fun stop() {
            preStop?.invoke()
            realStop()
        }
    }

    fun onStart(block: () -> Unit) {
        if (preStart != null) throw IllegalStateException()
        else preStart = block
    }

    fun finishWhen(block: () -> Boolean) {
        masterPredicate = block
    }

    fun onUpdate(block: () -> Unit) {
        if (preUpdate != null) throw IllegalStateException()
        else preUpdate = block
    }

    fun onStop(block: () -> Unit) {
        if (preStop != null) throw IllegalArgumentException()
        else preStop = block
    }
}