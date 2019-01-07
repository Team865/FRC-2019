package ca.warp7.frc2019.state

import ca.warp7.frc2019.state.drive.CheesyDrive
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frckt.ControllerState
import ca.warp7.frckt.RobotController

fun RobotController.cheesyDrive() {
    Drive.setState(CheesyDrive) {
        wheel = rightXAxis
        throttle = leftYAxis
        quickTurn = leftBumper == ControllerState.HeldDown
    }
}
