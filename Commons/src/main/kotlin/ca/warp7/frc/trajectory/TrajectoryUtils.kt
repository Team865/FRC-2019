package ca.warp7.frc.trajectory

import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.CurvatureState
import ca.warp7.frc.geometry.Pose2D
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.pow
import kotlin.math.sqrt

fun List<CurvatureState<Pose2D>>.timedTrajectory(
        model: DifferentialDriveModel,
        startVelocity: Double = 0.0,
        endVelocity: Double = 0.0,
        maxVelocity: Double = model.maxVelocity,
        maxAcceleration: Double = model.maxAcceleration
): List<TrajectoryPoint> {
    val constraints = map {
        val driveKinematicConstraint = model.signedMaxAtCurvature(it.curvature, maxVelocity).linear
        val k = abs(it.curvature)
        val centripetalAccelerationConstraint = if (k > 1E-5) maxAcceleration / k else maxVelocity
        minOf(driveKinematicConstraint, centripetalAccelerationConstraint)
    }
    val distances = zipWithNext { a, b ->
        val chordLength = (a.state.translation - b.state.translation).mag
        if (a.curvature.epsilonEquals(0.0)) chordLength else
            abs(asin(chordLength * a.curvature / 2) / a.curvature * 2)
    }
    val timedStates = map { TrajectoryPoint(it, maxVelocity, maxAcceleration) }
    timedStates.first().velocity = startVelocity
    val forwardMoments = Array(size) { 0.0 }
    for (i in 0 until size - 1) {
        val dist = distances[i]
        val now = timedStates[i]
        val next = timedStates[i + 1]
        val constrainedVelocity = constraints[i + 1]
        val maxReachableVelocity = sqrt(now.velocity.pow(2) + 2 * maxAcceleration * dist)
        next.velocity = minOf(next.velocity, constrainedVelocity, maxReachableVelocity)
        val t = (2 * dist) / (now.velocity + next.velocity)
        forwardMoments[i + 1] = t
    }
    timedStates.last().velocity = endVelocity
    val reverseMoments = Array(size) { 0.0 }
    for (i in size - 1 downTo 1) {
        val dist = distances[i - 1]
        val now = timedStates[i]
        val next = timedStates[i - 1]
        val constrainedVelocity = constraints[i - 1]
        val maxReachableVelocity = sqrt(now.velocity.pow(2) + 2 * maxAcceleration * dist)
        next.velocity = minOf(next.velocity, constrainedVelocity, maxReachableVelocity)
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