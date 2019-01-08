package ca.warp7.frc2019

import ca.warp7.frc.ControlLoop

object TestController : ControlLoop {

    override fun setup() {
        print("Robot State: Test")
    }

    override fun periodic() {
    }
}