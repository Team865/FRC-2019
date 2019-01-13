@file:Suppress("unused")

package ca.warp7.frc2019.states

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.Electrical

object ElectricalState {
    val Idle = runOnce { Electrical.compressor.closedLoopControl = false }
    val CompressorClosedLoop = runOnce { Electrical.compressor.closedLoopControl = true }
}