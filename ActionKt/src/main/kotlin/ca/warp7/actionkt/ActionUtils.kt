@file:Suppress("unused")

package ca.warp7.actionkt


fun <T> T.runOnce(block: T.() -> Unit) = object : Action {
    override fun start() = block(this@runOnce)
}

fun <T> T.periodic(block: T.() -> Unit) = object : Action {
    override fun update() = block(this@periodic)
    override val shouldFinish: Boolean
        get() = false
}

fun wait(seconds: Int) = wait(seconds.toDouble())

fun wait(seconds: Double) = action { finishWhen { elapsed > seconds } }