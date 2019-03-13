package ca.warp7.frc2019.test.drive.simple_spline_trajectory

import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.drive.solvedMaxAtCurvature
import ca.warp7.frc.path.*
import ca.warp7.frc2019.constants.DriveConstants

@Suppress("MemberVisibilityCanBePrivate", "unused")
class ContinuousSplineTrajectory(val path: Path2D, val model: DifferentialDriveModel) {
    // Number of segments
    val segments = 100
    // Parameter t of each segment
    val parametricDistance: Double = 1.0 / segments
    // Generate the path
    val points: List<Path2DState> = (0..segments).map { path[it * parametricDistance] }
    // Isolated constraints
    val curvatureConstraints = points.map { model.solvedMaxAtCurvature(it.curvature) }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ContinuousSplineTrajectory(QuinticSegment2D(
                    x0 = 0.0,
                    dx0 = 100.0,
                    ddx0 = 0.0,
                    x1 = 100.0,
                    dx1 = 0.0,
                    ddx1 = 0.0,
                    y0 = 0.0,
                    dy0 = 0.0,
                    ddy0 = 0.0,
                    y1 = 100.0,
                    dy1 = 100.0,
                    ddy1 = 0.0
            ), model = DifferentialDriveModel(
                    wheelbaseRadius = DriveConstants.kTurningDiameter / 2,
                    maxVelocity = DriveConstants.kMaxVelocity,
                    maxAcceleration = DriveConstants.kMaxAcceleration,
                    maxFreeSpeedVelocity = DriveConstants.kMaxFreeSpeedVelocity,
                    frictionVoltage = DriveConstants.kVIntercept
            )).apply {
                //curvatureConstraints.forEach { println(it) }
            }
        }
    }
}