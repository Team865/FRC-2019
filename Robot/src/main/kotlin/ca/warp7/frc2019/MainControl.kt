package ca.warp7.frc2019

import ca.warp7.frc.ControlLoop
import ca.warp7.frc.Controls
import ca.warp7.frc2019.state.drive.CheesyDrive.cheesyDrive

object MainControl : ControlLoop {

    override fun setup() {
        println("Robot State: Teleop")
    }

    override fun periodic() {

        with(Controls.driver) {
            cheesyDrive()
        }

        with(Controls.operator) {
            TODO()
        }
    }
}