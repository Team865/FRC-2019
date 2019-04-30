package ca.warp7.frc.trajectory

import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.CurvatureState
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.squared
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.sqrt

fun List<CurvatureState<Pose2D>>.timedTrajectory(
        model: DifferentialDriveModel,
        startVelocity: Double = 0.0,
        endVelocity: Double = 0.0,
        maxVelocity: Double = model.maxVelocity,
        maxAcceleration: Double = model.maxAcceleration,
        maxCentripetalAcceleration: Double = maxAcceleration
): List<TrajectoryPoint> {
    // Create list of states with everything set to max, then limit it afterwards
    val states = map { TrajectoryPoint(it, maxVelocity, maxAcceleration) }
    // Compute arc length between each pair of points in the path
    val arcLengths = zipWithNext { a, b ->
        val chordLength = (a.state.translation - b.state.translation).mag
        if (a.curvature.epsilonEquals(0.0)) chordLength else
            abs(asin(chordLength * a.curvature / 2) / a.curvature * 2)
    }
    // Compute isolated velocity constraints at a given curvature
    val isolatedConstraints = map {
        val k = abs(it.curvature)
        if (k.isNaN() || k.isInfinite()) return@map 0.0
        /* Velocity constrained by curvature equations
         * eqn 1. w = (right - left) / (2 * L)
         * eqn 2. v = (left + right) / 2
         * 1. Rearrange equation 1: w(2 * L) = right - left;  left = right - w(2 * L)
         * 2. Assuming the right side is at max velocity: right = V_max;  left = V_max - w(2 * L)
         * 3. Substitute left and right into equation 2: v = (2 * V_max - w(2 * L)) / 2
         * 5. Substitute w = v * k into equation 2, v = (2 * V_max - v * k * 2 * L) / 2
         * 6. Solve: v = V_max - v * k * L; v + v * k * L = V_max; v * (1 + k * L) = V_max; v = V_max / (1 + k * L)*/
        val driveKinematicConstraint = maxVelocity / (1 + k * model.wheelbaseRadius)
        // Velocity constrained inversely proportional to the curvature
        val centripetalAccelerationConstraint = if (k > 1E-5) maxCentripetalAcceleration / k else maxVelocity
        return@map minOf(driveKinematicConstraint, centripetalAccelerationConstraint)
    }
    // Assign the initial velocity
    states.first().velocity = startVelocity
    // Forward pass
    val forwardTiming = Array(size) { 0.0 }
    for (i in 0 until size - 1) {
        val arcLength = arcLengths[i]
        val currentState = states[i]
        val nextState = states[i + 1]
        val constrainedVelocity = isolatedConstraints[i + 1]
        // Apply kinematic equation vf^2 = vi^2 + 2ax, solve for vf
        val maxReachableVelocity = sqrt(currentState.velocity.squared + 2 * maxAcceleration * arcLength)
        // Limit velocity based on curvature constraint and forward acceleration
        nextState.velocity = minOf(nextState.velocity, constrainedVelocity, maxReachableVelocity)
        // Calculate the forward dt
        forwardTiming[i + 1] = (2 * arcLength) / (currentState.velocity + nextState.velocity)
    }
    // Assign the final velocity
    states.last().velocity = endVelocity
    // Reverse pass
    val reverseTiming = Array(size) { 0.0 }
    for (i in size - 1 downTo 1) {
        val arcLength = arcLengths[i - 1]
        val currentState = states[i]
        val nextState = states[i - 1]
        // Apply kinematic equation vf^2 = vi^2 + 2ax, solve for vf
        val maxReachableVelocity = sqrt(currentState.velocity.squared + 2 * maxAcceleration * arcLength)
        // Limit velocity based on reverse acceleration
        nextState.velocity = minOf(nextState.velocity, maxReachableVelocity)
        // Calculate the reverse dt
        reverseTiming[i] = (2 * arcLength) / (currentState.velocity + nextState.velocity)
    }
    // take the max of the forward dt and the reverse dt
    val maxedTiming = forwardTiming.zip(reverseTiming, Math::max).toTypedArray()
    // calculate acceleration based on final dt
    for (i in 0 until size - 2) {
        states[i + 1].acceleration = (states[i + 1].velocity - states[i].velocity) / maxedTiming[i + 1]
    }
    // Set the last state's acceleration to 0 to allow the motor to stop
    states.last().acceleration = 0.0
    // Integrate dt array into timestamps
    for (i in 1 until maxedTiming.size) {
        maxedTiming[i] += maxedTiming[i - 1]
        states[i].t = maxedTiming[i]
    }
    return states
}