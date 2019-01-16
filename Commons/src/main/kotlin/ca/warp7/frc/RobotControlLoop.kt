package ca.warp7.frc

/**
 * Defines a periodic procedure getting input from the controllers
 */
interface RobotControlLoop {
    fun setup()

    fun periodic()
}
