package ca.warp7.frckt

interface Action {
    fun start()
    fun shouldFinish()
    fun update()
    fun stop()
}