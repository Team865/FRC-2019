package ca.warp7.frc2019.followers

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.squared
import ca.warp7.frc.trajectory.TrajectoryController
import ca.warp7.frc.trajectory.TrajectoryState
import ca.warp7.frc2019.helper.velocity
import ca.warp7.frc2019.subsystems.Drive

class PurePursuit2Follower : TrajectoryFollower {

    override fun updateTrajectory(
            controller: TrajectoryController,
            setpoint: TrajectoryState,
            error: Pose2D
    ) {
        val lookahead = PurePursuit1Follower.getLookahead(controller, setpoint)
        val initialToRobot = setpoint.pose - error
        val velocity = setpoint.velocity
        val y = (lookahead.pose.translation - initialToRobot.translation)
                .rotate(initialToRobot.rotation.inverse).y
        val l = (lookahead.pose - initialToRobot).log().mag()
        val curvature = (2 * y) / l.squared
        Drive.setAdjustedCurvature(velocity, curvature, error.translation.x)
    }
}