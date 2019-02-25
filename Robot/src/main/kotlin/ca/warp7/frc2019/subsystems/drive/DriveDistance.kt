package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.abs

object DriveDistance : Action {
    var distance = 0.0 // distance in inches
    var tolerance = 1E-2 //distance

    private var initialDistance: Double = 0.0

    override fun start() {
        Drive.controlMode = ControlMode.Position
        val leftInitialSensorTicks = Drive.leftPositionTicks
        val rightInitialSensorTicks = Drive.rightPositionTicks
        initialDistance = DriveConstants.kWheelCircumference * ((leftInitialSensorTicks + rightInitialSensorTicks) / 2.0) / 1024

        Drive.leftDemand = 1024 * distance / DriveConstants.kWheelCircumference + leftInitialSensorTicks
        Drive.rightDemand = 1024 * distance / DriveConstants.kWheelCircumference + rightInitialSensorTicks
    }

    override val shouldFinish
        get() = abs(distance - ((Drive.leftPositionTicks + Drive.rightPositionTicks) / 2.0
                - initialDistance)) <= tolerance
}