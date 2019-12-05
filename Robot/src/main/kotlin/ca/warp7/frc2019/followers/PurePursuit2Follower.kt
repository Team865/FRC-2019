package ca.warp7.frc2019.followers

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.squared
import ca.warp7.frc.trajectory.TrajectoryController
import ca.warp7.frc.trajectory.TrajectoryFollower
import ca.warp7.frc.trajectory.TrajectoryState
import ca.warp7.frc2019.subsystems.Drive

class PurePursuit2Follower : TrajectoryFollower {

    override fun updateTrajectory(
            controller: TrajectoryController,
            setpoint: TrajectoryState,
            error: Pose2D
    ) {
        val lookahead = PurePursuit1Follower.getLookahead(controller, setpoint)
        val initialToRobot = controller.getInitialToRobot(Drive.robotState)
        val velocity = setpoint.velocity
        val y = (lookahead.arcPose.translation - initialToRobot.translation)
                .rotate(initialToRobot.rotation.inverse).y
        val l = (lookahead.arcPose.pose - initialToRobot).logFast().mag()
        val curvature = (2 * y) / l.squared
        Drive.setAdjustedCurvature(velocity, curvature, error.translation.x)
    }
}