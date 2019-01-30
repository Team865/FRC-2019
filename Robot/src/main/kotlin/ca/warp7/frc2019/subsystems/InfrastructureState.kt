package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce

object InfrastructureState {
    val kInfrastructureSetup = runOnce {
        Navx.set(NavxState.kWaitForCalibration)
    }
}