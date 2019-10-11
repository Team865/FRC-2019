package ca.warp7.frc2019.followers

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.rotate
import ca.warp7.frc.squared
import ca.warp7.frc.trajectory.TrajectoryController
import ca.warp7.frc.trajectory.TrajectoryFollower
import ca.warp7.frc.trajectory.TrajectoryState
import ca.warp7.frc2019.subsystems.Drive

class PurePursuit2Follower : TrajectoryFollower {

    companion object {
        const val kPathLookaheadTime = 0.4  // seconds to look ahead along the path for steering
        const val kMinLookDist = 24.0  // inches
        const val kLookaheadSearchDt = 0.01
    }

    override fun updateTrajectory(
            controller: TrajectoryController,
            setpoint: TrajectoryState,
            error: Pose2D
    ) {
        val lookahead = PurePursuit1Follower.getLookahead(controller, setpoint)
        val initialToRobot = controller.getInitialToRobot(Drive.robotState)
        val velocity = setpoint.velocity
        val y = (lookahead.arcPose.translation - initialToRobot.translation).rotate(-initialToRobot.rotation).y
        val l = initialToRobot.distanceTo(lookahead.arcPose.pose)
        val curvature = (2 * y) / l.squared
        Drive.setAdjustedCurvature(velocity, curvature, error.translation.x)
    }
}