package ca.warp7.frc.control

import edu.wpi.first.wpilibj.XboxController

class ControlInput {

    private val mutableDriver = RobotControllerImpl()
    private val mutableOperator = RobotControllerImpl()

    private val xboxDriver = XboxController(0)
    private val xboxOperator = XboxController(1)

    // expose the immutable interface

    val driver: RobotController = mutableDriver
    val operator: RobotController = mutableOperator

    // update methods

    fun updateDriver() {
        mutableDriver.updateWith(xboxDriver)
    }

    fun updateOperator() {
        mutableOperator.updateWith(xboxOperator)
    }
}