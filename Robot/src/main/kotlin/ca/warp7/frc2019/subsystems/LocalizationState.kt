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
        val average = (leftDiff + rightDiff) / 2.0
        Localization.predictedState += mat[average * cos(Navx.yaw), average * sin(Navx.yaw), 0.0, 0.0]
        Localization.predictedState[2] = Navx.yaw
        Localization.predictedState[3] = 0.0
    }
}