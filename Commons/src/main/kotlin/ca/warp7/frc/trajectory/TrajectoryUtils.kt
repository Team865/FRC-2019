package ca.warp7.frc.trajectory

import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.drive.signedMaxAtCurvature
import ca.warp7.frc.f
import ca.warp7.frc.geometry.CurvatureState
import ca.warp7.frc.geometry.Pose2D
import kotlin.math.pow
import kotlin.math.sqrt

fun List<CurvatureState<Pose2D>>.timedTrajectory(
        model: DifferentialDriveModel,
        startVelocity: Double = 0.0,
        endVelocity: Double = 0.0
): List<TimedConstraints> {
    val curvatureConstraints = map { model.signedMaxAtCurvature(it.curvature) }
    val distances = zipWithNext { a, b ->
        (a.state.translation - b.state.translation).mag
    }
    val timedStates = map { TimedConstraints(it, model.maxVelocity, model.maxAcceleration) }
    timedStates.first().velocity = startVelocity
    val forwardMoments = Array(size) { 0.0 }
    for (i in 0 until size - 1) {
        val dist = distances[i]
        val now = timedStates[i]
        val next = timedStates[i + 1]
        val c = curvatureConstraints[i + 1]
        val maxAcceleration = model.maxAcceleration
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
        val maxAcceleration = model.maxAcceleration
        val maxLinear = sqrt(now.velocity.pow(2) + 2 * maxAcceleration * dist)
        next.velocity = minOf(next.velocity, c.linear, maxLinear)
        val t = (2 * dist) / (now.velocity + next.velocity)
        reverseMoments[i - 1] = t
    }
    val totalMoments = forwardMoments.zip(reverseMoments, Math::max).toTypedArray()
    for (i in 0 until totalMoments.size - 1) {
        timedStates[i + 1].acceleration = (timedStates[i + 1].velocity - timedStates[i].velocity) / totalMoments[i]
    }
    for (i in 1 until totalMoments.size) {
        totalMoments[i] += totalMoments[i - 1]
        timedStates[i].t = totalMoments[i]
    }
    return timedStates
}

data class TimedConstraints(
        var state: CurvatureState<Pose2D>,
        var velocity: Double = 0.0,
        var acceleration: Double = 0.0,
        var t: Double = 0.0
) {
    override fun toString(): String {
        return "Timed(t=${t.f}, $state, v=${velocity.f}, a=${acceleration.f})"
    }
}