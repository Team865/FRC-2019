package ca.warp7.frc2019.actions

import ca.warp7.frc.action.Action
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.degrees
import ca.warp7.frc.log.CSVLogger
import ca.warp7.frc.trajectory.TrajectoryBuilder
import ca.warp7.frc.trajectory.TrajectoryController
import ca.warp7.frc.trajectory.TrajectoryFollower
import ca.warp7.frc2019.followers.AnglePIDFollower
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.Timer
import kotlin.math.abs

class DriveTrajectory2(func: TrajectoryBuilder.() -> Unit) : Action {


    constructor(distance: Double) : this({
        moveTo(Pose2D.identity)
        moveTo(Pose2D(abs(distance), 0.0, Rotation2D.identity))
        setFollower(AnglePIDFollower())
    })

    val controller = TrajectoryController(func)

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

    private val updateNotifier = Notifier(this::onNotifierLoop)

    val TrajectoryController.follower: TrajectoryFollower get() = TODO()

    fun onNotifierLoop() {
        val newTime = Timer.getFPGATimestamp()
        val dt = newTime - time
        time = newTime
        val robotState = Drive.robotState

        // Update trajectory

        val setpoint = controller.advanceTrajectory(dt)
        val error = controller.getError(robotState, setpoint.arcPose)
        controller.follower.updateTrajectory(controller, setpoint, error)

        // Logging

        val setpointState = setpoint.arcPose

        val data = arrayOf(
                // "t"
                setpoint.t,
                setpoint.v, setpoint.dv, setpoint.arcPose.curvature,
                // "s_x", "s_y", "s_theta",
                setpointState.translation.x, setpointState.translation.y, setpointState.rotation.degrees,
                // "r_x", "r_y", "r_theta"
                robotState.translation.x, robotState.translation.y, robotState.rotation.degrees,
                // "left", "right"
                io.leftFeedforward * 12.0, io.rightFeedforward * 12.0
        )

        logger.writeData(*data)
        // println(data.joinToString("\t") { it.f })
    }

    override fun firstCycle() {
        controller.initTrajectory(arrayOf(), absolute = true, optimizeDkSquared = true, robotState = Pose2D.identity)
    }

    override fun update() {
        if (!notifierStarted && controller.tryFinishGeneratingTrajectory()) { // Wait on the generator future task
            updateNotifier.startPeriodic(0.01)
            time = Timer.getFPGATimestamp()
            notifierStarted = true
        }
    }

    override fun shouldFinish(): Boolean {
        return notifierStarted && Drive.isDoneTrajectory()
    }

    override fun interrupt() {
        if (notifierStarted) updateNotifier.stop()
        Drive.neutralOutput()
    }
}