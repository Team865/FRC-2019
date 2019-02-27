package ca.warp7.frc2019.test.drive.velocity_control

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.feetToMeters
import ca.warp7.frc.geometry.Interpolator
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.geometry.minus
import ca.warp7.frc.geometry.rangeTo
import ca.warp7.frc.path.IsolatedConstraint
import ca.warp7.frc.path.Moment
import ca.warp7.frc.path.TimedState
import ca.warp7.frc2019.constants.DriveConstants
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

@Suppress("MemberVisibilityCanBePrivate", "unused")
class LineTrajectory {
    val maxVelocity = feetToMeters(DriveConstants.kMaxVelocity)
    val maxAcceleration = feetToMeters(DriveConstants.kMaxAcceleration)

    val initialState: Translation2D = Translation2D.identity

    // Frame of reference: positive x is the front of the robot
    val targetState: Translation2D = Translation2D(x = feetToMeters(8.0), y = 0.0)

    // Parameter t of each segment
    val segmentLength: Double = DriveConstants.kSegmentLength / (targetState - initialState).mag
    val segmentCount: Int = (1 / segmentLength).toInt() + 1

    // Interpolator between current state and target state
    val diff: Interpolator<Translation2D> = initialState..targetState

    // Generate the path
    val path: List<Translation2D> = (0 until segmentCount).map { diff[it * segmentLength] }

    // Generate a list of timed states
    val timedStates: List<TimedState<Translation2D>> = path.map { TimedState(it) }

    // Generate moments with 0 for time, velocity and acceleration
    val moments: List<Moment<TimedState<Translation2D>>>

    // Create start and end velocity constraints
    val constraints: List<IsolatedConstraint> = listOf(
            IsolatedConstraint(moment = 0, velocity = 0.0),
            IsolatedConstraint(moment = segmentCount - 1, velocity = 0.0)
    )

    init {
        // Apply constraints to trajectory
        constraints.forEach {
            timedStates[it.moment].apply {
                velocity = it.velocity
                constrained = true
            }
        }
        // Forward pass
        val forwardMoments = Array(segmentCount) { 0.0 }
        for (i in 0 until segmentCount - 2) {
            val currentMoment = timedStates[i]
            val nextMoment = timedStates[i + 1]
            val vi = currentMoment.velocity
            val ds = (nextMoment.state - currentMoment.state).mag
            val vf = min(sqrt(vi.pow(2) + 2 * maxAcceleration * ds), maxVelocity)
            val af = if (vf.epsilonEquals(vi)) 0.0 else maxAcceleration
            forwardMoments[i + 1] = ds / vf
            if (!nextMoment.constrained) nextMoment.velocity = vf
            nextMoment.acceleration = af
        }
//        // Backward pass
//        val backwardMoments = Array(segmentCount) { 0.0 }
//        for (i in segmentCount - 1 downTo 1) {
//            val currentMoment = timedStates[i]
//            val nextMoment = timedStates[i - 1]
//            val vi = currentMoment.velocity
//            val vf = min(sqrt(vi.pow(2) + 2 * maxAcceleration *
//                    (nextMoment.state - currentMoment.state).mag), maxVelocity).withSign(vi)
//            val af = (if (vf.epsilonEquals(vi)) 0.0 else maxAcceleration).withSign(vi)
//            backwardMoments[i - 1] = (vf - vi) / af
//            if (!nextMoment.constrained) nextMoment.velocity = vf
//            nextMoment.acceleration = af
//        }
        println(forwardMoments.toList())
//        println(backwardMoments.toList())
        val forwardMomentsSum = forwardMoments.copyOf()
        for (i in 1 until forwardMomentsSum.size) {
            forwardMomentsSum[i] += forwardMomentsSum[i - 1]
        }
        moments = forwardMomentsSum/*.zip(backwardMoments, Math::max)*/
                .mapIndexed { i, d -> Moment(d, timedStates[i]) }
    }
}