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
import kotlin.math.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
class ContinuousSplineTrajectory(val path: Path2D, val model: DifferentialDriveModel) {
    // Number of segments
    val segments = 200
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
                (radius * 2 * asin(length / (2 * radius)))
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
        val forwardMoments = Array(segments + 1) { 0.0 }
        for (i in 0 until segments) {
            val leftDist = dL[i]
            val rightDist = dR[i]
            val now = timedStates[i]
            val next = timedStates[i + 1]
            val constraint = curvatureConstraints[i + 1]
            val maxLeftVel = sqrt(now.leftVelocity.pow(2) + 2 * model.maxAcceleration * leftDist)
            val maxRightVel = sqrt(now.rightVelocity.pow(2) + 2 * model.maxAcceleration * rightDist)
            var leftVel = min(maxLeftVel, constraint.left)
            var rightVel = min(maxRightVel, constraint.right)
            if (leftVel > rightVel && constraint.left > constraint.right) {
                rightVel = maxLeftVel / constraint.left * constraint.right
            } else if (leftVel < rightVel && constraint.left < constraint.right) {
                leftVel = maxRightVel / constraint.right * constraint.left
            }
            val leftAcc = (leftVel.pow(2) - now.leftVelocity.pow(2)) / (2 * leftDist)
            val rightAcc = (rightVel.pow(2) - now.rightVelocity.pow(2)) / (2 * rightDist)
            val d = (leftDist + rightDist) / 2
            val vi = (now.leftVelocity + now.rightVelocity) / 2
            val vf = (leftVel + rightVel) / 2
            val t = (2 * d) / (vi + vf)
            next.leftVelocity = leftVel
            next.rightVelocity = rightVel
            next.leftAcceleration = leftAcc
            next.rightAcceleration = rightAcc
            forwardMoments[i + 1] = t
        }
        println(model)
        println()
        val dLx = Array(segments + 1) { 0.0 }
        for (ll in 0 until segments) {
            dLx[ll + 1] = dLx[ll] + dL[ll]
        }
        val dRx = Array(segments + 1) { 0.0 }
        for (rr in 0 until segments) {
            dRx[rr + 1] = dRx[rr] + dR[rr]
        }
        for (j in 0..segments) {
            val p = points[j]
            val c = curvatureConstraints[j]
            val s = timedStates[j]
            val l = dLx[j].s
            val r = dRx[j].s
            //println("($l, $r), (${s.leftVelocity.s}, ${s.rightVelocity.s}), (${s.leftAcceleration.s}, ${s.rightAcceleration.s}) , $c, ${model.solve(c)}, ${s.state}, ${p.curvature.s}")
        }
        for (i in 1 until forwardMoments.size) {
            forwardMoments[i] += forwardMoments[i - 1]
        }
        moments = forwardMoments.mapIndexed { i, d -> Moment(d, timedStates[i]) }
    }

    val Double.s get() = "%.3f".format(this)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ContinuousSplineTrajectory(QuinticSegment2D(
                    x0 = 0.0,
                    y0 = 0.0,

                    x1 = 10.0,
                    y1 = 0.0,

                    dx0 = 1.0,
                    dy0 = 0.0,

                    ddx0 = 0.0,
                    ddy0 = 0.0,

                    dx1 = 1.0,
                    dy1 = 0.0,

                    ddx1 = 0.0,
                    ddy1 = 0.0
            ), model = DifferentialDriveModel(
                    wheelbaseRadius = DriveConstants.kTurningDiameter / 2,
                    maxVelocity = DriveConstants.kMaxVelocity,
                    maxAcceleration = DriveConstants.kMaxAcceleration,
                    maxFreeSpeedVelocity = DriveConstants.kMaxFreeSpeedVelocity,
                    frictionVoltage = DriveConstants.kVIntercept
            )).apply {
                //timedStates.map { it.leftVelocity }.forEach { println(it) }
                moments.forEach { println(it) }
            }
        }
    }
}