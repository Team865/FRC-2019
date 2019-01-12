package ca.warp7.frc2019

import ca.warp7.frc.ControlLoop

object TestControl : ControlLoop {

    override fun setup() {
        print("Robot State: Test")
    }

    override fun periodic() {
    }
}