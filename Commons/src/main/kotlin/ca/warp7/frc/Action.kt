package ca.warp7.frc

interface Action {
    fun start() = Unit
    fun shouldFinish(): Boolean = true
    fun update() = Unit
    fun stop() = Unit
}