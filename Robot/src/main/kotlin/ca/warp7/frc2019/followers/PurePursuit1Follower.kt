package ca.warp7.frc2019.followers

import ca.warp7.frc.geometry.*
import ca.warp7.frc.trajectory.TrajectoryController
import ca.warp7.frc.trajectory.TrajectoryFollower
import ca.warp7.frc.trajectory.TrajectoryState
import ca.warp7.frc2019.subsystems.Drive

class PurePursuit1Follower : TrajectoryFollower {

    companion object {
        const val kPathLookaheadTime = 0.4  // seconds to look ahead along the path for steering
        const val kMinLookDist = 24.0  // inches
        const val kLookaheadSearchDt = 0.01

        fun getLookahead(controller: TrajectoryController, setpoint: TrajectoryState): TrajectoryState {
            var lookaheadTime = kPathLookaheadTime
            var lookahead = controller.interpolatedTimeView(lookaheadTime)
            var lookaheadDistance = setpoint.arcPose.distanceTo(lookahead.arcPose)
            while (lookaheadDistance < kMinLookDist && (controller.totalTime - controller.t) > lookaheadTime) {
                lookaheadTime += kLookaheadSearchDt
                lookahead = controller.interpolatedTimeView(lookaheadTime)
                lookaheadDistance = setpoint.arcPose.distanceTo(lookahead.arcPose)
            }
            if (lookaheadDistance < kMinLookDist) lookahead = controller.trajectory.last()
            return lookahead
        }
    }

    fun getDirection(pose: Pose2D, point: ArcPose2D): Double {
        val poseToPoint = point.translation - pose.translation
        val robot = pose.rotation.translation
        return if (robot cross poseToPoint < 0.0) -1.0 else 1.0 // if robot < pose turn left
    }

    fun findCenter(pose: Pose2D, point: ArcPose2D): Translation2D {
        val poseToPointHalfway = pose.translation.interpolate(point.translation, 0.5)
        val normal = pose.translation.inverse.transform(poseToPointHalfway).direction.normal
        val perpendicularBisector = Pose2D(poseToPointHalfway, normal)
        val normalFromPose = Pose2D(pose.translation, pose.rotation.normal)
        return if (normalFromPose.isColinear(perpendicularBisector.run { Pose2D(translation, rotation.normal) })) {
            // Special case: center is poseToPointHalfway.
            poseToPointHalfway
        } else normalFromPose.intersection(perpendicularBisector)
    }

    fun findRadius(pose: Pose2D, point: ArcPose2D): Double {
        return (point.translation - findCenter(pose, point)).mag * getDirection(pose, point)
    }

    override fun updateTrajectory(
            controller: TrajectoryController,
            setpoint: TrajectoryState,
            error: Pose2D
    ) {
        val lookahead = getLookahead(controller, setpoint)
        val initialToRobot = controller.getInitialToRobot(Drive.robotState)
        val velocity = setpoint.velocity
        val curvature = 1.0 / findRadius(initialToRobot, lookahead.arcPose)
        Drive.setAdjustedCurvature(velocity, curvature, error.translation.x)
    }
}