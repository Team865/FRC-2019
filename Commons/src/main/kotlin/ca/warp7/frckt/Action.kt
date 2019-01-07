package ca.warp7.frckt

interface Action {
    fun start()
    fun shouldFinish(): Boolean
    fun update()
    fun stop()
}