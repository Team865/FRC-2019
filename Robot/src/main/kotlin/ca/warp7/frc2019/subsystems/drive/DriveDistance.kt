package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import kotlin.math.abs

object DriveDistance : Action {
    var distance = 0.0 // distance in inches
    var closeEnough = 1E-2

    override fun start() {
        Drive.outputMode = Drive.OutputMode.Position
        Drive.onZeroSensors()

        Drive.leftDemand = 1024 * distance / DriveConstants.kWheelCircumference
        Drive.rightDemand = 1024 * distance / DriveConstants.kWheelCircumference
    }

    override val shouldFinish
        get() = abs(distance - Drive.distanceFromZero) <= closeEnough
}