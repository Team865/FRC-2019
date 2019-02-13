package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.periodic

object InfrastructureState {
    val kStateMonitor = Infrastructure.periodic {
        if (compressor.pressureSwitchValue && !compressor.enabled()) compressor.start()
    }
}
