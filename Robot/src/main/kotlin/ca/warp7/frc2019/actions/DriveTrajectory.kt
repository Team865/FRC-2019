package ca.warp7.frc2019.actions

import ca.warp7.actionkt.Action
import ca.warp7.frc.PID
import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.rotate
import ca.warp7.frc.interpolate
import ca.warp7.frc.path.parameterizedSplinesOf
import ca.warp7.frc.trajectory.TrajectoryPoint
import ca.warp7.frc.trajectory.timedTrajectory
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.subsystems.Drive

class DriveTrajectory(
        waypoints: Array<Pose2D>,
        backwards: Boolean = false,
        velRatio: Double = 0.75,
        accRatio: Double = 0.85,
        val feedback: Boolean = false,
        val velocityPD: Boolean = false
) : Action {

    private val robot: Pose2D get() = Drive.robotState
    private val io: RobotIO = RobotIO
    private val direction: Double = if (backwards) -1.0 else 1.0
    private val trajectory: List<TrajectoryPoint> = parameterizedSplinesOf(*waypoints).timedTrajectory(
            model = Drive.model,
            startVelocity = 0.0,
            endVelocity = 0.0,
            maxVelocity = Drive.model.maxVelocity * velRatio,
            maxAcceleration = Drive.model.maxAcceleration * accRatio)
    private val totalTime: Double = trajectory.last().t
    private var t = 0.0

    override fun start() {
        io.drivePID = if (velocityPD) PID(kP = 0.8, kD = 5.0) else PID()
        Drive.prevVelocity = ChassisState(0.0, 0.0)
        Drive.setVelocity(0.0, 0.0, 0.0, 0.0)
    }

    override fun update() {
        // advance the trajectory
        t += io.dt
        var index = 0
        while (index < trajectory.size - 2 && trajectory[index + 1].t < t) index++
        val last = trajectory[index]
        val next = trajectory[index + 1]

        // interpolate the setpoint and integrate velocity
        val x = if (last.t.epsilonEquals(next.t)) 1.0 else (t - last.t) / (next.t - last.t)
        val a = direction * interpolate(last.acceleration, next.acceleration, x)
        val v = direction * last.velocity + a * (t - last.t)
        val k = interpolate(last.state.curvature, next.state.curvature, x)
        val position = last.state.state.translation.interpolate(next.state.state.translation, x)
        val heading = last.state.state.rotation.interpolate(next.state.state.rotation, x)

        // solve for and set desired dynamic state
        val vel = ChassisState(v, v * k)
        val acc = ChassisState(a, a * k)
        val error = Pose2D((position - robot.translation).rotate(-robot.rotation), (heading - robot.rotation))
        val desiredDynamicState = if (feedback) Drive.updateRamsete(error, vel) else Drive.model.solve(vel, acc)

        Drive.setDynamicState(desiredDynamicState)
    }

    override val shouldFinish: Boolean get() = t > totalTime

    override fun stop() {
        Drive.setVelocity(0.0, 0.0, 0.0, 0.0)
    }
}