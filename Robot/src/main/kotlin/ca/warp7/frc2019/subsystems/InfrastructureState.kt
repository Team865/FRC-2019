package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.periodic

object InfrastructureState {
    val kInfrastructureSetup = periodic {
        if (!Infrastructure.calibrated && !Infrastructure.ahrs.isCalibrating) {
            Infrastructure.calibrated = true
        }
    }
}
