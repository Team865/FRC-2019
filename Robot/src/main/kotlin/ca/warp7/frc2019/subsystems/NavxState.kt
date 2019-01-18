package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.periodic
import ca.warp7.actionkt.runOnce

object NavxState {
    val kWaitForCalibration = periodic {
        if (!Navx.ahrs.isCalibrating) {
            Navx.calibrated = true
            Navx.set(kNormal)
        }
    }

    private val kNormal = runOnce { }
}