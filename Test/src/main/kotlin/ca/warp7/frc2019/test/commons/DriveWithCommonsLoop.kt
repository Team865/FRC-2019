package ca.warp7.frc2019.test.commons

import ca.warp7.actionkt.Action
import ca.warp7.frc.ControllerState
import ca.warp7.frc.withDriver
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.drive.DriveState

object DriveWithCommonsLoop : Action {

    override fun update() {
        withDriver {
            Drive.set(DriveState.kAlignedCurvature) {
                xSpeed = leftYAxis
                zRotation = rightXAxis
                isQuickTurn = leftBumper == ControllerState.HeldDown
            }
        }
    }
}