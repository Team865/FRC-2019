package ca.warp7.frc2019.actions

import ca.warp7.frc.action.Action
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.degrees
import ca.warp7.frc.log.CSVLogger
import ca.warp7.frc2019.constants.DriveFollower
import ca.warp7.frc2019.constants.DriveFollower.*
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.Timer
import kotlin.math.abs

class DriveTrajectory(
        val waypoints: Array<Pose2D>,
        val maxVelocity: Double = 0.8 * Drive.model.maxVelocity,
        val maxAcceleration: Double = 0.7 * Drive.model.maxAcceleration,
        val maxCentripetalAcceleration: Double = maxAcceleration,
        val backwards: Boolean = false,
        val absolute: Boolean = false,
        val enableJerkLimiting: Boolean = false,
        val optimizeDkSquared: Boolean = false,
        val follower: DriveFollower = VoltageOnly
) : Action {

    constructor(distance: Double) : this(
            arrayOf(Pose2D.identity, Pose2D(abs(distance), 0.0, Rotation2D.identity)),
            backwards = distance < 0,
            follower = AnglePID
    )

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

    private val updateNotifier = Notifier {
        val newTime = Timer.getFPGATimestamp()
        val dt = newTime - time
        time = newTime
        val setpoint = Drive.advanceTrajectory(dt)
        val error = Drive.getError(setpoint.arcPose)
        val velocity = setpoint.velocity
        val acceleration = setpoint.acceleration
        when (follower) {
            VoltageOnly -> Drive.setVoltage(velocity, acceleration)
            SpeedDemand -> Drive.setDynamics(velocity, acceleration)
            PosePID -> Drive.updatePosePID(error, velocity, acceleration)
            AnglePID -> Drive.updateAnglePID(velocity, acceleration)
            SimplePurePursuit -> Drive.updateSimplePurePursuit(error, setpoint)
            PurePursuit -> Drive.updatePurePursuit(error, setpoint)
            Ramsete -> Drive.updateRamsete(error, velocity)
        }
        val setpointState = setpoint.arcPose
        val robotState = Drive.robotState
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

    override fun start() {
        Drive.initTrajectory(waypoints, maxVelocity, maxAcceleration, maxCentripetalAcceleration,
                backwards, absolute, enableJerkLimiting, optimizeDkSquared)
    }

    override fun update() {
        if (!notifierStarted && Drive.tryFinishGeneratingTrajectory()) { // Wait on the generator future task
            updateNotifier.startPeriodic(0.01)
            time = Timer.getFPGATimestamp()
            notifierStarted = true
        }
    }

    override val shouldFinish: Boolean get() = notifierStarted && Drive.isDoneTrajectory()

    override fun stop() {
        if (notifierStarted) updateNotifier.stop()
        Drive.neutralOutput()
    }
}