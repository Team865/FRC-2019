package ca.warp7.frckt

interface Action {
    fun start() = Unit
    fun shouldFinish(): Boolean = true
    fun update() = Unit
    fun stop() = Unit
}