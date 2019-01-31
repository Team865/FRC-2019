package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.periodic
import koma.extensions.set
import koma.mat
import kotlin.math.cos
import kotlin.math.sin

object LocalizationState {
    val kDisplacementOnly = periodic {
        val leftDiff = Drive.leftPositionTicks - Drive.prevLeftPositionTicks
        val rightDiff = Drive.rightPositionTicks - Drive.prevRightPositionTicks
        val avg = (leftDiff + rightDiff) / 2.0
        Localization.apply {
            predictedState += mat[avg * cos(Infrastructure.yaw), avg * sin(Infrastructure.yaw), 0.0, 0.0]
            predictedState[2] = Infrastructure.yaw
            predictedState[3] = 0.0
        }
    }
}