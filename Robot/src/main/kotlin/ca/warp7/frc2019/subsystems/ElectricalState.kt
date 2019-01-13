@file:Suppress("unused")

package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce

object ElectricalState {
    val Idle = runOnce { Electrical.compressor.closedLoopControl = false }
    val CompressorClosedLoop = runOnce { Electrical.compressor.closedLoopControl = true }
}