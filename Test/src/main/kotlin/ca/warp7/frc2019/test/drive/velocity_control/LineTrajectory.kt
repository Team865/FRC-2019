package ca.warp7.frc2019.test.drive.velocity_control

import ca.warp7.actionkt.Action
import ca.warp7.frc.feetToMeters
import ca.warp7.frc.geometry.Interpolator
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.geometry.minus
import ca.warp7.frc.geometry.rangeTo
import ca.warp7.frc2019.constants.DriveConstants

@Suppress("MemberVisibilityCanBePrivate", "unused")
object LineTrajectory : Action {
    val maxVelocity = feetToMeters(DriveConstants.kMaxVelocity) * 0.8
    val maxAcceleration = feetToMeters(DriveConstants.kMaxAcceleration)

    val initialState: Translation2D = Translation2D.identity

    // Frame of reference: positive x is the front of the robot
    val targetState: Translation2D = Translation2D(x = feetToMeters(5.0), y = 0.0)

    // Parameter t of each segment
    val segmentLength: Double = DriveConstants.kSegmentLength / (targetState - initialState).mag
    val segmentCount: Int = (1 / segmentLength).toInt() + 1

    // Interpolator between current state and target state
    val diff: Interpolator<Translation2D> = initialState..targetState

    // Generate the path
    val path: List<Translation2D> = (0..segmentCount).map { diff[it * segmentLength] }
}