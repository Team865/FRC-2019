package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.periodic
import ca.warp7.frc2019.math.Pose2d
import kotlin.math.cos
import kotlin.math.sin

object LocalizationState {
    val DisplacementOnly = periodic {
        val leftDiff = Drive.leftPositionTicks - Drive.prevLeftPositionTicks
        val rightDiff = Drive.rightPositionTicks - Drive.prevRightPositionTicks
        val average = (leftDiff + rightDiff) / 2.0
        Localization.predictedPose += Pose2d(average * cos(Navx.yaw), average * sin(Navx.yaw), 0.0)
        Localization.predictedPose.heading = Navx.yaw
    }
}