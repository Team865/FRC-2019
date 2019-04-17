package ca.warp7.frc.trajectory

import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.geometry.CurvatureState
import ca.warp7.frc.geometry.Pose2D
import kotlin.math.pow
import kotlin.math.sqrt

fun List<CurvatureState<Pose2D>>.timedTrajectory(
        model: DifferentialDriveModel,
        startVelocity: Double = 0.0,
        endVelocity: Double = 0.0,
        maxVelocity: Double = model.maxVelocity,
        maxAcceleration: Double = model.maxAcceleration
): List<TrajectoryPoint> {
    val curvatureConstraints = map { model.signedMaxAtCurvature(it.curvature, maxVelocity) }
    val distances = zipWithNext { a, b ->
        (a.state.translation - b.state.translation).mag
    }
    val timedStates = map { TrajectoryPoint(it, maxVelocity, maxAcceleration) }
    timedStates.first().velocity = startVelocity
    val forwardMoments = Array(size) { 0.0 }
    for (i in 0 until size - 1) {
        val dist = distances[i]
        val now = timedStates[i]
        val next = timedStates[i + 1]
        val c = curvatureConstraints[i + 1]
        val maxLinear = sqrt(now.velocity.pow(2) + 2 * maxAcceleration * dist)
        next.velocity = minOf(next.velocity, c.linear, maxLinear)
        val t = (2 * dist) / (now.velocity + next.velocity)
        forwardMoments[i + 1] = t
    }
    timedStates.last().velocity = endVelocity
    val reverseMoments = Array(size) { 0.0 }
    for (i in size - 1 downTo 1) {
        val dist = distances[i - 1]
        val now = timedStates[i]
        val next = timedStates[i - 1]
        val c = curvatureConstraints[i - 1]
        val maxLinear = sqrt(now.velocity.pow(2) + 2 * maxAcceleration * dist)
        next.velocity = minOf(next.velocity, c.linear, maxLinear)
        val t = (2 * dist) / (now.velocity + next.velocity)
        reverseMoments[i] = t
    }
    val moments = forwardMoments.zip(reverseMoments, Math::max).toTypedArray()
    for (i in 0 until size - 2) {
        timedStates[i + 1].acceleration = (timedStates[i + 1].velocity - timedStates[i].velocity) / moments[i + 1]
    }
    timedStates.last().acceleration = 0.0
    for (i in 1 until moments.size) {
        moments[i] += moments[i - 1]
        timedStates[i].t = moments[i]
    }
    return timedStates
}