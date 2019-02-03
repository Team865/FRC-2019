package ca.warp7.actionkt

interface Action {
    fun start() = Unit
    val shouldFinish: Boolean get() = true
    fun update() = Unit
    fun stop() = Unit
}