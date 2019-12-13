package ca.warp7.frc2019.followers

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.findRadius
import ca.warp7.frc.trajectory.TrajectoryController
import ca.warp7.frc.trajectory.TrajectoryState
import ca.warp7.frc2019.helper.velocity
import ca.warp7.frc2019.subsystems.Drive

class PurePursuit1Follower : TrajectoryFollower {

    companion object {
        const val kPathLookaheadTime = 0.4  // seconds to look ahead along the path for steering
        const val kMinLookDist = 24.0  // inches
        const val kLookaheadSearchDt = 0.01

        fun getLookahead(controller: TrajectoryController, setpoint: TrajectoryState): TrajectoryState {
            var lookaheadTime = kPathLookaheadTime
            var lookahead = controller.interpolatedTimeView(lookaheadTime)
            var lookaheadDistance = (lookahead.pose - setpoint.pose).log().mag()
            while (lookaheadDistance < kMinLookDist && (controller.totalTime - controller.t) > lookaheadTime) {
                lookaheadTime += kLookaheadSearchDt
                lookahead = controller.interpolatedTimeView(lookaheadTime)
                lookaheadDistance = (lookahead.pose - setpoint.pose).log().mag()
            }
            if (lookaheadDistance < kMinLookDist) lookahead = controller.trajectory.last()
            return lookahead
        }
    }

    override fun updateTrajectory(
            controller: TrajectoryController,
            setpoint: TrajectoryState,
            error: Pose2D
    ) {
        val lookahead = getLookahead(controller, setpoint)
        val initialToRobot = setpoint.pose - error
        val velocity = setpoint.velocity
        val curvature = 1.0 / findRadius(initialToRobot, lookahead.pose)
        Drive.setAdjustedCurvature(velocity, curvature, error.translation.x)
    }
}