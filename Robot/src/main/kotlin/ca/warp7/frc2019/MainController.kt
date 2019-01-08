package ca.warp7.frc2019

import ca.warp7.frc2019.state.drive.CheesyDrive.cheesyDrive
import ca.warp7.frc.ControlLoop
import ca.warp7.frc.driver

object MainController : ControlLoop {

    override fun setup() {
        println("Robot State: Teleop")
    }

    override fun periodic() {

        with(driver) {
            cheesyDrive()
        }
    }
}