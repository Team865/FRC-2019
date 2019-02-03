package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.periodic

object LocalizationState {
    val kDisplacementOnly = periodic {
        val leftDiff = Drive.leftPositionTicks - Drive.prevLeftPositionTicks
        val rightDiff = Drive.rightPositionTicks - Drive.prevRightPositionTicks
        @Suppress("UNUSED_VARIABLE") val avg = (leftDiff + rightDiff) / 2.0
    }
}