package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.CurvatureState
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.squared
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.sqrt

fun List<CurvatureState<Pose2D>>.timedTrajectory(
        wheelbaseRadius: Double,
        startVelocity: Double,
        endVelocity: Double,
        maxVelocity: Double,
        maxAcceleration: Double,
        maxCentripetalAcceleration: Double,
        maxJerk: Double = 60.0
): List<TrajectoryPoint> {
    // Create list of states with everything set to max, then limit it afterwards
    val states = map { TrajectoryPoint(it, maxVelocity, maxAcceleration) }
    // Compute arc length between each pair of points in the path
    val arcLengths = zipWithNext { a, b ->
        val k = abs(a.curvature)
        // get the chord length (translational distance)
        val chordLength = a.state.translation.distanceTo(b.state.translation)
        when {
            // Going straight, arcLength = chordLength
            k < 1E-6 -> chordLength
            // Turning in place, arcLength = 0.0
            k.isInfinite() -> 0.0
            // Moving on a radius:
            // arcLength = theta * r = theta / k
            // theta = asin(half_chord / r) * 2 = asin(half_chord * k) * 2
            // arcLength = asin(half_chord * k) * 2 / k
            else -> asin((chordLength / 2) * a.curvature) * 2 / a.curvature
        }
    }
    // Compute isolated velocity constraints at a given curvature
    val isolatedConstraints = map {
        val k = abs(it.curvature)
        // Turning in place, v = 0
        if (k.isInfinite()) return@map 0.0
        /* Velocity constrained by curvature equations
         * eqn 1. w = (right - left) / (2 * L)
         * eqn 2. v = (left + right) / 2
         * 1. Rearrange equation 1: w(2 * L) = right - left;  left = right - w(2 * L)
         * 2. Assuming the right side is at max velocity: right = V_max;  left = V_max - w(2 * L)
         * 3. Substitute left and right into equation 2: v = (2 * V_max - w(2 * L)) / 2
         * 5. Substitute w = v * k into equation 2, v = (2 * V_max - v * k * 2 * L) / 2
         * 6. Solve: v = V_max - v * k * L; v + v * k * L = V_max; v * (1 + k * L) = V_max; v = V_max / (1 + k * L)*/
        val driveKinematicConstraint = maxVelocity / (1 + k * wheelbaseRadius)
        // Velocity constrained inversely proportional to the curvature
        val centripetalAccelerationConstraint = if (k > 1E-6) maxCentripetalAcceleration / k else maxVelocity
        return@map minOf(driveKinematicConstraint, centripetalAccelerationConstraint)
    }
    // Assign the initial velocity
    states.first().velocity = startVelocity
    // Forward pass
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
        nextState.t = (2 * arcLength) / (currentState.velocity + nextState.velocity)
    }
    // Assign the final velocity
    states.last().velocity = endVelocity
    // Reverse pass
    for (i in size - 1 downTo 1) {
        val arcLength = arcLengths[i - 1]
        val currentState = states[i]
        val nextState = states[i - 1]
        // Apply kinematic equation vf^2 = vi^2 + 2ax, solve for vf
        val maxReachableVelocity = sqrt(currentState.velocity.squared + 2 * maxAcceleration * arcLength)
        // Limit velocity based on reverse acceleration
        nextState.velocity = minOf(nextState.velocity, maxReachableVelocity)
        // Calculate the reverse dt
        currentState.t = maxOf(currentState.t, (2 * arcLength) / (currentState.velocity + nextState.velocity))
    }
    // calculate acceleration based on final dt
    for (i in 0 until size - 1) {
        val currentState = states[i + 1]
        val lastState = states[i]
        currentState.acceleration = (currentState.velocity - lastState.velocity) / currentState.t
        currentState.jerk = (currentState.acceleration - lastState.acceleration) / currentState.t
    }
    // Limit jerk if it's enabled
    if (maxJerk.isFinite()) {
        val jerkPoints = mutableListOf<Int>()
        for (i in 1 until size) if (abs(states[i].jerk) > maxJerk) jerkPoints.add(i)
        for (i in 0 until jerkPoints.size) {
            println()
            val si = jerkPoints[i]
            val range = abs(states[si].jerk / (2 * maxJerk)).toInt() * 2 + 1
            val accLast = states[si - 1].acceleration
            val start = maxOf(if (i == 0) 0 else jerkPoints[i - 1], si - range)
            val end = minOf(if (i == jerkPoints.size - 1) states.size - 1 else jerkPoints[i + 1], si + range)
            val jerkStep = states[si].jerk * states[si].t / (end - start)
            var t = 0.0
            for (j in start..end) {
                val next = states[j + 1]
                next.jerk = jerkStep
                t += next.t
                next.acceleration = accLast + jerkStep * (j - start)
            }
        }
    }
    // Set the endpoints' acceleration to 0 to allow the motor to stop
    states.first().acceleration = 0.0
    states.last().acceleration = 0.0
    // Integrate dt array into timestamps
    for (i in 1 until states.size) states[i].t += states[i - 1].t
    return states
}