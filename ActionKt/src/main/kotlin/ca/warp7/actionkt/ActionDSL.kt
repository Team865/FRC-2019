package ca.warp7.actionkt

class ActionDSL {

    private var start: (() -> Unit)? = null
    private var update: (() -> Unit)? = null
    private var stop: (() -> Unit)? = null
    private var predicate: (() -> Boolean)? = null

    fun toAction(): Action {
        return runOnce { }
    }

    fun onStart(block: () -> Unit) {
        if (start != null) throw IllegalStateException()
        else start = block
    }

    fun finishWhen(block: () -> Boolean) {
        if (predicate != null) throw IllegalStateException()
        else predicate = block
    }

    fun onUpdate(block: () -> Unit) {
        if (update != null) throw IllegalStateException()
        else update = block
    }

    fun onStop(block: () -> Unit) {
        if (stop != null) throw IllegalArgumentException()
        else stop = block
    }
}