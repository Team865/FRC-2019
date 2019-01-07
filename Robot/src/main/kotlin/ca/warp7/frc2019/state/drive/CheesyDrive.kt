package ca.warp7.frc2019.state.drive

import ca.warp7.frc2019.constants.DriveConstants.kCheesyDriveDeadband
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frckt.Action

object CheesyDrive : Action {

    private val calculator = ca.warp7.frc.CheesyDrive(this::setPercent)

    init {
        calculator.disableInternalDeadband()
    }

    private var leftPercent = 0.0
    private var rightPercent = 0.0

    var wheel = 0.0
    var throttle = 0.0
    var quickTurn = false
    var solenoidOnForShifter = false

    private fun linearScaleDeadband(n: Double) = if (Math.abs(n) < kCheesyDriveDeadband) 0.0
    else (n - Math.copySign(kCheesyDriveDeadband, n)) / (1 - kCheesyDriveDeadband)

    @Synchronized
    private fun setPercent(left: Double, right: Double) {
        leftPercent = left
        rightPercent = right
    }

    @Synchronized
    override fun start() = Unit

    @Synchronized
    override fun shouldFinish() = false

    @Synchronized
    override fun update() {
        calculator.cheesyDrive(linearScaleDeadband(wheel), linearScaleDeadband(throttle), quickTurn)
        Drive.leftDemand = leftPercent
        Drive.rightDemand = rightPercent
    }
}