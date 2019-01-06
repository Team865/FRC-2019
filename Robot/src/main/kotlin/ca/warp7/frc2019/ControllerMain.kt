package ca.warp7.frc2019

import ca.warp7.frc.ControlLoop
import ca.warp7.frckt.driver
import ca.warp7.frckt.operator

object ControllerMain : ControlLoop {

    override fun setup() {
    }

    override fun periodic() {

        with(driver) {
            leftYAxis
        }

        with(operator) {
            leftXAxis
        }
    }
}