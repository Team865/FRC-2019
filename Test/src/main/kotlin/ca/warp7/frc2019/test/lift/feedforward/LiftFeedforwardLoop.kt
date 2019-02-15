package ca.warp7.frc2019.test.lift.feedforward

import ca.warp7.frc.ControllerState
import ca.warp7.frc.RobotControlLoop
import ca.warp7.frc.withDriver
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.DriveState

object LiftFeedforwardLoop : RobotControlLoop {
    override fun setup() {
    }

    override fun periodic() {
        withDriver {
            Drive.set(DriveState.kCurvature) {
                xSpeed = leftYAxis
                zRotation = rightXAxis
                isQuickTurn = leftBumper == ControllerState.HeldDown
            }
        }
    }
}