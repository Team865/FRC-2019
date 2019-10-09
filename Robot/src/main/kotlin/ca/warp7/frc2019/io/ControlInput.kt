package ca.warp7.frc2019.io

import ca.warp7.frc.input.ControllerImpl
import ca.warp7.frc.input.RobotController
import edu.wpi.first.wpilibj.GenericHID.Hand.kLeft
import edu.wpi.first.wpilibj.GenericHID.Hand.kRight
import edu.wpi.first.wpilibj.XboxController

class ControlInput {

    private val mutableDriver = ControllerImpl()
    private val mutableOperator = ControllerImpl()

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

    fun ControllerImpl.updateWith(c: XboxController) {
        with(c) {
            updateAxes(
                    leftTrigger = getTriggerAxis(kLeft),
                    rightTrigger = getTriggerAxis(kRight),
                    leftX = getX(kLeft),
                    leftY = getY(kLeft),
                    rightX = getX(kRight),
                    rightY = getY(kRight)
            )
            updateButtons(
                    aButton = aButton,
                    bButton = bButton,
                    xButton = xButton,
                    yButton = yButton,
                    leftBumper = getBumper(kLeft),
                    rightBumper = getBumper(kRight),
                    leftStickButton = getStickButton(kLeft),
                    rightStickButton = getStickButton(kRight),
                    startButton = startButton,
                    backButton = backButton
            )
        }
    }

    var driverIsXbox = false
    var operatorIsXbox = false


    fun reset() {
        mutableDriver.reset()
        mutableOperator.reset()
    }
}