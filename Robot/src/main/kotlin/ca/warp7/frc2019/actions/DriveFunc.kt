package ca.warp7.frc2019.actions

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc2019.followers.AnglePIDFollower
import kotlin.math.abs

@Suppress("UNUSED_PARAMETER", "FunctionName")
fun driveStraight(
        distanceInFeet: Double,
        isBackwards: Boolean = distanceInFeet < 0
) = DriveTrajectory2(AnglePIDFollower()) {
    moveTo(Pose2D.identity)
    moveTo(Pose2D(abs(distanceInFeet / 12.0), 0.0, Rotation2D.identity))
}