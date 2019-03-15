package ca.warp7.frc2019.test.drive.simple_spline_trajectory

import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.drive.TankTrajectoryState
import ca.warp7.frc.drive.WheelState
import ca.warp7.frc.drive.solvedMaxAtCurvature
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.minus
import ca.warp7.frc.path.*
import ca.warp7.frc.trajectory.Moment
import ca.warp7.frc2019.constants.DriveConstants
import kotlin.math.absoluteValue
import kotlin.math.asin
import kotlin.math.sqrt
import kotlin.math.withSign

@Suppress("MemberVisibilityCanBePrivate", "unused")
class ContinuousSplineTrajectory(val path: Path2D, val model: DifferentialDriveModel) {
    // Number of segments
    val segments = 100
    // Parameter t of each segment
    val parametricDistance: Double = 1.0 / segments
    // Generate the path
    val points: List<Path2DState> = (0..segments).map { path[it * parametricDistance] }
    // Isolated constraints
    val curvatureConstraints: List<WheelState> = points.map { model.solvedMaxAtCurvature(it.curvature) }
    // timed states
    val timedStates: List<TankTrajectoryState<Pose2D>> = points.map { TankTrajectoryState(it.toPose()) }
    // Distances
    val dL: List<Double>
    val dR: List<Double>
    // moments
    val moments: List<Moment<TankTrajectoryState<Pose2D>>>

    init {
        dL = (0 until segments).map {
            val p0 = points[it]
            val length = (points[it + 1].position - p0.position).mag
            val curvature = p0.curvature
            if (curvature.epsilonEquals(0.0)) length else {
                val radius = 1 / curvature.absoluteValue - model.wheelbaseRadius.withSign(curvature)
                radius * 2 * asin(length / (2 * radius))
            }
        }
        dR = (0 until segments).map {
            val p0 = points[it]
            val length = (points[it + 1].position - p0.position).mag
            val curvature = p0.curvature
            if (curvature.epsilonEquals(0.0)) length else {
                val radius = 1 / curvature.absoluteValue + model.wheelbaseRadius.withSign(curvature)
                radius * 2 * asin(length / (2 * radius))
            }
        }
        for (i in 0 until segments) {
            val dl = dL[i]
            val dr = dR[i]
            val now = timedStates[i]
            val next = timedStates[i + 1]
            val constraint = curvatureConstraints[i + 1]
            var left = sqrt(now.leftVelocity + 2 * model.maxAcceleration * dl)
            var right = sqrt(now.rightVelocity + 2 * model.maxAcceleration * dr)
            if (left > constraint.left) {
                left = constraint.left
                right = left / constraint.left * constraint.right
            }
            if (right > constraint.right) {
                right = constraint.right
                left = right / constraint.right * constraint.left
            }
            if (constraint.left > constraint.right) {
                right = left / constraint.left * constraint.right
            } else if (constraint.right > constraint.left) {
                left = right / constraint.right * constraint.left
            }
            val leftAcc = (left - now.leftVelocity) / (2 * dl)
            val rightAcc = (right - now.rightVelocity) / (2 * dr)
            next.leftVelocity = left
            next.rightVelocity = right
            next.leftAcceleration = leftAcc
            next.rightAcceleration = rightAcc
        }
        moments = emptyList()
    }

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
                    y1 = -60.0,
                    dy1 = 100.0,
                    ddy1 = 0.0
            ), model = DifferentialDriveModel(
                    wheelbaseRadius = DriveConstants.kTurningDiameter / 2,
                    maxVelocity = DriveConstants.kMaxVelocity,
                    maxAcceleration = DriveConstants.kMaxAcceleration,
                    maxFreeSpeedVelocity = DriveConstants.kMaxFreeSpeedVelocity,
                    frictionVoltage = DriveConstants.kVIntercept
            )).apply {
                timedStates.map { it.leftVelocity }.forEach { println(it) }
            }
        }
    }
}