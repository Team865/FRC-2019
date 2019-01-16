package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.periodic
import ca.warp7.actionkt.runOnce

object NavxState {
    val WaitForCalibration = periodic {
        if (!Navx.ahrs.isCalibrating) Navx.set(Normal)
    }

    private val Normal = runOnce { }
}