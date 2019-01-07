package ca.warp7.frc2019

import ca.warp7.frc.ControlLoop
import ca.warp7.frc2019.state.cheesyDrive
import ca.warp7.frc2019.state.drive.CheesyDrive
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frckt.ControllerState
import ca.warp7.frckt.driver
import ca.warp7.frckt.operator

object MainController : ControlLoop {

    override fun setup() {
        println("Robot State: Teleop")

        Drive.setState(CheesyDrive)
    }

    override fun periodic() {

        with(driver) {
            cheesyDrive {
                wheel = rightXAxis
                throttle = leftYAxis
                quickTurn = leftBumper == ControllerState.HeldDown
            }
        }

        with(operator) {
            TODO()
        }
    }
}