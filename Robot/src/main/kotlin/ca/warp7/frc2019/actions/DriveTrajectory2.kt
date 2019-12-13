package ca.warp7.frc2019.actions

import ca.warp7.frc.action.Action
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.log.CSVLogger
import ca.warp7.frc.trajectory.TrajectoryBuilder
import ca.warp7.frc.trajectory.TrajectoryController
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.followers.TrajectoryFollower
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.Timer

class DriveTrajectory2(
        val follower: TrajectoryFollower,
        builder: TrajectoryBuilder
) : Action {

    constructor(
            follower: TrajectoryFollower,
            func: TrajectoryBuilder.() -> Unit
    ) : this(follower, TrajectoryBuilder()
            .setMaxVelocity(DriveConstants.kMaxVelocity)
            .setMaxAcceleration(DriveConstants.kMaxAcceleration)
            .setMaxCentripetalAcceleration(DriveConstants.kMaxAcceleration)
            .setBendFactor(1.2)
            .noJerkLimit()
            .apply(func)
    )

    val controller = TrajectoryController(builder)

    private val io: BaseIO = ioInstance()

    private val logger: CSVLogger = io.getLogger("DriveTrajectory")
            .withHeaders(
                    "t",
                    "s_v", "s_a", "s_k",
                    "s_x", "s_y", "s_theta",
                    "r_x", "r_y", "r_theta",
                    "left", "right")

    var time = 0.0
    var notifierStarted = false

    var offset = Pose2D.identity

    private val updateNotifier = Notifier(this::onNotifierLoop)

    fun onNotifierLoop() {
        val newTime = Timer.getFPGATimestamp()
        val dt = newTime - time
        time = newTime
        val robotState = Drive.robotState

        // Update trajectory

        val setpoint = controller.advanceTrajectory(dt)
        val error = setpoint.pose - (robotState - offset)
        follower.updateTrajectory(controller, setpoint, error)

        // Logging

        val setpointState = setpoint.pose

        val data = arrayOf(
                // "t"
                setpoint.t,
                setpoint.v, setpoint.dv, setpoint.curvature,
                // "s_x", "s_y", "s_theta",
                setpointState.translation.x, setpointState.translation.y, setpointState.rotation.degrees(),
                // "r_x", "r_y", "r_theta"
                robotState.translation.x, robotState.translation.y, robotState.rotation.degrees(),
                // "left", "right"
                io.leftFeedforward * 12.0, io.rightFeedforward * 12.0
        )

        logger.writeData(*data)
        // println(data.joinToString("\t") { it.f })
    }

    override fun firstCycle() {
        controller.initTrajectory()
    }

    override fun update() {
        if (!notifierStarted && controller.tryFinishGeneratingTrajectory()) { // Wait on the generator future task
            updateNotifier.startPeriodic(0.01)
            time = Timer.getFPGATimestamp()
            offset = Drive.robotState - controller.trajectory[0].pose
            notifierStarted = true
        }
    }

    override fun shouldFinish(): Boolean {
        return notifierStarted && controller.isDoneTrajectory()
    }

    override fun interrupt() {
        if (notifierStarted) {
            updateNotifier.stop()
        }
        Drive.neutralOutput()
    }
}