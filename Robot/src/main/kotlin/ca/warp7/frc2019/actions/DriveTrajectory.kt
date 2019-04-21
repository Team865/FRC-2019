package ca.warp7.frc2019.actions

import ca.warp7.actionkt.Action
import ca.warp7.frc.PID
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.path.parameterizedSplinesOf
import ca.warp7.frc.trajectory.TrajectoryPoint
import ca.warp7.frc.trajectory.timedTrajectory
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.constants.FollowerType
import ca.warp7.frc2019.constants.FollowerType.*
import ca.warp7.frc2019.subsystems.Drive

class DriveTrajectory(
        waypoints: Array<Pose2D>,
        velRatio: Double = 0.75,
        accRatio: Double = 0.85,
        val backwards: Boolean = false,
        val resetState: Boolean = true,
        val followerType: FollowerType = FeedforwardOnly
) : Action {

    private val io: RobotIO = RobotIO

    private val trajectory: List<TrajectoryPoint> = parameterizedSplinesOf(*waypoints).timedTrajectory(
            model = Drive.model,
            startVelocity = 0.0,
            endVelocity = 0.0,
            maxVelocity = Drive.model.maxVelocity * velRatio,
            maxAcceleration = Drive.model.maxAcceleration * accRatio)

    override fun start() {
        Drive.initTrajectory(trajectory, resetState, backwards, PID(kP = 0.8, kD = 5.0))
    }

    override fun update() {
        val setpoint = Drive.advanceTrajectory(io.dt)
        when (followerType) {
            FeedforwardOnly -> Drive.setFeedforward(setpoint.chassisVelocity, setpoint.chassisAcceleration)
            VelocityPD -> Drive.setDynamicState(setpoint.chassisVelocity, setpoint.chassisAcceleration)
            Ramsete -> Drive.updateRamsete(Drive.getError(setpoint.state.state), setpoint.chassisVelocity)
        }
    }

    override val shouldFinish: Boolean get() = Drive.isDoneTrajectory()

    override fun stop() {
        Drive.neutralOutput()
    }
}