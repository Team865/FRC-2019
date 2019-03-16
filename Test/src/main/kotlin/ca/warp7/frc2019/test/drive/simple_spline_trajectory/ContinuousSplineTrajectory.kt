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
    val segments = 200
    val parametricDistance: Double = 1.0 / segments
    val points: List<Path2DState> = (0..segments).map { path[it * parametricDistance] }
    val curvatureConstraints: List<WheelState> = points.map { model.solvedMaxAtCurvature(it.curvature) }
    val timedStates: List<TankTrajectoryState<Pose2D>> = points.map { TankTrajectoryState(it.toPose(), it.curvature) }
    val dL: List<Double> = (0 until segments).map {
        val p0 = points[it]
        val length = (points[it + 1].position - p0.position).mag
        val curvature = p0.curvature
        if (curvature.epsilonEquals(0.0)) length else {
            val radius = 1 / curvature.absoluteValue - model.wheelbaseRadius.withSign(curvature)
            (radius * 2 * asin(length / (2 * radius)))
        }
    }
    val dR: List<Double> = (0 until segments).map {
        val p0 = points[it]
        val length = (points[it + 1].position - p0.position).mag
        val curvature = p0.curvature
        if (curvature.epsilonEquals(0.0)) length else {
            val radius = 1 / curvature.absoluteValue + model.wheelbaseRadius.withSign(curvature)
            radius * 2 * asin(length / (2 * radius))
        }
    }
    val moments: List<Moment<TankTrajectoryState<Pose2D>>>

    init {
        timedStates.first().apply {
            leftVelocity = 0.0
            rightVelocity = 0.0
        }
        val forwardMoments = Array(segments + 1) { 0.0 }
        for (i in 0 until segments) {
            val leftDist = dL[i]
            val rightDist = dR[i]
            val now = timedStates[i]
            val next = timedStates[i + 1]
            val c = curvatureConstraints[i + 1]
            if (i == segments - 1) {
                println("$leftDist, $rightDist, $c\n $now\n $next")
            }
            val maxLeft = sqrt(now.leftVelocity.pow(2) + 2 * model.maxAcceleration * leftDist)
            val maxRight = sqrt(now.rightVelocity.pow(2) + 2 * model.maxAcceleration * rightDist)
            var leftVel = min(maxLeft, c.left)
            var rightVel = min(maxRight, c.right)
            if (leftVel > rightVel && c.left > c.right) rightVel = maxLeft / c.left * c.right
            else if (leftVel < rightVel && c.left < c.right) leftVel = maxRight / c.right * c.left
            val vi = (now.leftVelocity + now.rightVelocity) / 2
            val vf = (leftVel + rightVel) / 2
            val t = (leftDist + rightDist) / (vi + vf)
            next.leftVelocity = leftVel
            next.rightVelocity = rightVel
            forwardMoments[i + 1] = t
            if (i == segments - 1) {
                println("$leftDist, $rightDist, $c\n $now\n $next")
            }
        }
        timedStates.last().apply {
            leftVelocity = 0.0
            rightVelocity = 0.0
        }
        val backwardMoments = Array(segments + 1) { 0.0 }
        for (i in segments downTo 1) {
            val leftDist = dL[i - 1]
            val rightDist = dR[i - 1]
            val now = timedStates[i]
            val next = timedStates[i - 1]
            val c = curvatureConstraints[i - 1]
            if (i == segments - 1) {
                println("$leftDist, $rightDist, $c\n $now\n $next")
            }
            val forwardLeftMax = next.leftVelocity
            val forwardRightMax = next.rightVelocity
            val maxLeft = sqrt(now.leftVelocity.pow(2) + 2 * model.maxAcceleration * leftDist)
            val maxRight = sqrt(now.rightVelocity.pow(2) + 2 * model.maxAcceleration * rightDist)
            var leftVel = min(maxLeft, forwardLeftMax)
            var rightVel = min(maxRight, forwardRightMax)
            if (leftVel > rightVel && c.left > c.right) rightVel = maxLeft / c.left * c.right
            else if (leftVel < rightVel && c.left < c.right) leftVel = maxRight / c.right * c.left
            val vi = (now.leftVelocity + now.rightVelocity) / 2
            val vf = (leftVel + rightVel) / 2
            val t = (leftDist + rightDist) / (vi + vf)
            next.leftVelocity = leftVel
            next.rightVelocity = rightVel
            backwardMoments[i - 1] = t
            if (i == segments - 1) {
                println("$leftDist, $rightDist, $c\n $now\n $next")
            }
        }
        val totalMoments = forwardMoments.zip(backwardMoments, Math::max).toTypedArray()
        //forwardMoments.zip(backwardMoments).zip(totalMoments).forEach { println(it) }
        for (i in 1 until totalMoments.size) {
            timedStates[i].leftAcceleration = (timedStates[i].leftVelocity -
                    timedStates[i - 1].leftVelocity) / totalMoments[i]
            timedStates[i].rightAcceleration = (timedStates[i].rightVelocity -
                    timedStates[i - 1].rightVelocity) / totalMoments[i]
            totalMoments[i] += totalMoments[i - 1]
        }
        moments = totalMoments.mapIndexed { i, d -> Moment(d, timedStates[i]) }
    }

    val Double.s get() = "%.3f".format(this)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ContinuousSplineTrajectory(QuinticSegment2D(
                    x0 = 5.5,
                    y0 = 3.5,

                    x1 = 18.0,
                    y1 = 12.0,

                    dx0 = 20.0,
                    dy0 = 0.0,

                    ddx0 = 0.0,
                    ddy0 = 0.0,

                    dx1 = 18.0,
                    dy1 = 15.0,

                    ddx1 = 0.0,
                    ddy1 = 0.0
            ), model = DifferentialDriveModel(
                    wheelbaseRadius = DriveConstants.kEffectiveWheelBaseRadius,
                    maxVelocity = DriveConstants.kMaxVelocity,
                    maxAcceleration = DriveConstants.kMaxAcceleration,
                    maxFreeSpeedVelocity = DriveConstants.kMaxFreeSpeedVelocity,
                    frictionVoltage = DriveConstants.kVIntercept
            )).apply {
                //moments.forEach { println("(${it.t}, ${it.v.state.translation.x}, ${it.v.state.translation.y}),") }
                //moments.forEach { println(it) }
            }
        }
    }
}