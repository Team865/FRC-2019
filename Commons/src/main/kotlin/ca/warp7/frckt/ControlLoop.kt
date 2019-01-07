package ca.warp7.frckt

/**
 * Defines a periodic procedure getting input from the controllers
 */
interface ControlLoop {
    fun setup()

    fun periodic()
}
