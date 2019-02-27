package ca.warp7.frc2019.test.drive.velocity_control

import ca.warp7.actionkt.Action
import ca.warp7.frc.feetToMeters
import ca.warp7.frc.geometry.Interpolator
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.geometry.minus
import ca.warp7.frc.geometry.rangeTo
import ca.warp7.frc.path.IsolatedConstraint
import ca.warp7.frc.path.Moment
import ca.warp7.frc.path.TimedState
import ca.warp7.frc2019.constants.DriveConstants

@Suppress("MemberVisibilityCanBePrivate", "unused")
class LineTrajectory : Action {
    val maxVelocity = feetToMeters(DriveConstants.kMaxVelocity) * 0.8
    val maxAcceleration = feetToMeters(DriveConstants.kMaxAcceleration)

    val initialState: Translation2D = Translation2D.identity

    // Frame of reference: positive x is the front of the robot
    val targetState: Translation2D = Translation2D(x = feetToMeters(5.0), y = 0.0)

    // Parameter t of each segment
    val segmentLength: Double = DriveConstants.kSegmentLength / (targetState - initialState).mag
    val segmentCount: Int = (1 / segmentLength).toInt() + 1

    // Interpolator between current state and target state
    val diff: Interpolator<Translation2D> = initialState..targetState

    // Generate the path
    val path: List<Translation2D> = (0..segmentCount).map { diff[it * segmentLength] }

    // Generate a list of timed states
    val timedStates: List<TimedState<Translation2D>> = path.map { TimedState(it) }

    // Generate moments with 0 for time, velocity and acceleration
    val moments: List<Moment<TimedState<Translation2D>>>

    // Create start and end velocity constraints
    val constraints: List<IsolatedConstraint> = listOf(
            IsolatedConstraint(moment = 0, velocity = 0.0),
            IsolatedConstraint(moment = segmentCount, velocity = 0.0)
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
        var constrainedVelocity = 0.0
        val forwardMoments = Array(segmentCount) { 0.0 }
        for (i in 0..segmentCount - 2) {
            val currentMoment = timedStates[i]
            val nextMoment = timedStates[i + 1]
            if (currentMoment.constrained) {
                constrainedVelocity = currentMoment.velocity
            }
            val translation = nextMoment.state - currentMoment.state
        }
        moments = listOf()
    }
}