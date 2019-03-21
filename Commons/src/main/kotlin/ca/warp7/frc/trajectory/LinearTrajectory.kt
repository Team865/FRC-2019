package ca.warp7.frc.trajectory

import ca.warp7.frc.drive.DifferentialDriveModel
import ca.warp7.frc.drive.LinearTrajectoryState
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.Interpolator
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.geometry.minus
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

@Suppress("MemberVisibilityCanBePrivate", "unused")
class LinearTrajectory(distance: Double = 0.0, model: DifferentialDriveModel) {
    val maxVelocity = model.maxVelocity
    val maxAcceleration = model.maxAcceleration

    val initialState: Translation2D = Translation2D.identity

    // Frame of reference: positive x is the front of the robot
    val targetState: Translation2D = Translation2D(x = distance, y = 0.0)

    // Parameter t of each segment
    val segmentLength: Double = 0.01 / (targetState - initialState).mag
    val segmentCount: Int = (1 / segmentLength).toInt() + 1

    // Interpolator between current state and target state
    val diff: Interpolator<Translation2D> = initialState..targetState

    // Generate the path
    val path: List<Translation2D> = (0 until segmentCount).map { diff[it * segmentLength] }

    // Generate a list of timed states
    val timedStates: List<LinearTrajectoryState<Translation2D>> = path.map { LinearTrajectoryState(it) }

    // Generate moments with 0 for time, velocity and acceleration
    val moments: List<Moment<LinearTrajectoryState<Translation2D>>>

    // Create start and end velocity constraints
    val constraints: List<IsolatedConstraint> = listOf(
            IsolatedConstraint(moment = 0, velocity = 0.0),
            IsolatedConstraint(moment = segmentCount - 1, velocity = 0.0)
    )

    init {
        constraints.forEach {
            timedStates[it.moment].apply {
                velocity = it.velocity
                constrained = true
            }
        }
        val forwardMoments = Array(segmentCount) { 0.0 }
        for (i in 0 until segmentCount - 2) {
            val currentMoment = timedStates[i]
            val nextMoment = timedStates[i + 1]
            val vi = currentMoment.velocity
            val ds = (nextMoment.state - currentMoment.state).mag
            if (ds.epsilonEquals(0.0)) continue
            val vf = min(sqrt(vi.pow(2) + 2 * maxAcceleration * ds), maxVelocity)
            val dt = ds / vf
            forwardMoments[i + 1] = dt
            if (!nextMoment.constrained) nextMoment.velocity = vf
        }
        val backwardMoments = Array(segmentCount) { 0.0 }
        for (i in segmentCount - 1 downTo 1) {
            val currentMoment = timedStates[i]
            val nextMoment = timedStates[i - 1]
            val vi = currentMoment.velocity
            val ds = (nextMoment.state - currentMoment.state).mag
            if (ds.epsilonEquals(0.0)) continue
            val vf = min(sqrt(vi.pow(2) + 2 * maxAcceleration * ds), maxVelocity)
            val dt = ds / vf
            backwardMoments[i - 1] = dt
            if (!nextMoment.constrained) nextMoment.velocity = min(nextMoment.velocity, vf)
        }
        val totalMoments = forwardMoments.zip(backwardMoments, Math::max).toTypedArray()
        for (i in 1 until totalMoments.size) {
            timedStates[i].acceleration = (timedStates[i].velocity - timedStates[i - 1].velocity) / totalMoments[i]
            totalMoments[i] += totalMoments[i - 1]
        }
        moments = totalMoments.mapIndexed { i, d -> Moment(d, timedStates[i]) }
    }
}