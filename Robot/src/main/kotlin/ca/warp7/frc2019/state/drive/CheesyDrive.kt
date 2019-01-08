package ca.warp7.frc2019.state.drive

import ca.warp7.frc2019.constants.DriveConstants.kCheesyDriveDeadband
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frckt.Action
import ca.warp7.frckt.CheesyDriveCalculator
import ca.warp7.frckt.ControllerState
import ca.warp7.frckt.RobotController

object CheesyDrive : Action {

    private val calculator = CheesyDriveCalculator { left, right ->
        leftPercent = left
        rightPercent = right
    }

    private var leftPercent = 0.0
    private var rightPercent = 0.0

    private var wheel = 0.0
    private var throttle = 0.0
    private var quickTurn = false

    private fun linearScaleDeadband(n: Double): Double {
        return if (Math.abs(n) < kCheesyDriveDeadband) 0.0
        else (n - Math.copySign(kCheesyDriveDeadband, n)) / (1 - kCheesyDriveDeadband)
    }

    override fun start() = Unit
    override fun shouldFinish() = false

    override fun update() {
        calculator.cheesyDrive(linearScaleDeadband(wheel), linearScaleDeadband(throttle), quickTurn)
        Drive.leftDemand = leftPercent
        Drive.rightDemand = rightPercent
    }

    fun RobotController.cheesyDrive() {
        Drive.setState(CheesyDrive) {
            wheel = rightXAxis
            throttle = leftYAxis
            quickTurn = leftBumper == ControllerState.HeldDown
        }
    }
}