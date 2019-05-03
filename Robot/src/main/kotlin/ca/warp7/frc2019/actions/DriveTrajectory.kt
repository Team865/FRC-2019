package ca.warp7.frc2019.actions

import ca.warp7.actionkt.Action
import ca.warp7.frc.CSVLogger
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.radians
import ca.warp7.frc2019.constants.DriveFollower
import ca.warp7.frc2019.constants.DriveFollower.*
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive
import kotlin.math.abs

class DriveTrajectory(
        val waypoints: Array<Pose2D>,
        val maxVelocity: Double = 0.8 * Drive.model.maxVelocity,
        val maxAcceleration: Double = 0.8 * Drive.model.maxAcceleration,
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

    override fun start() {
        Drive.initTrajectory(waypoints, maxVelocity, maxAcceleration, maxCentripetalAcceleration,
                backwards, absolute, enableJerkLimiting, optimizeDkSquared)
    }

    override fun update() {
        val setpoint = Drive.advanceTrajectory(io.dt)
        val error = Drive.getError(setpoint.state.state)
        when (follower) {
            VoltageOnly -> Drive.setVoltage(setpoint.chassisVelocity, setpoint.chassisAcceleration)
            SpeedDemand -> Drive.setDynamics(setpoint.chassisVelocity, setpoint.chassisAcceleration)
            PosePID -> Drive.updatePosePID(error, setpoint.chassisVelocity, setpoint.chassisAcceleration)
            AnglePID -> Drive.updateAnglePID(setpoint.chassisVelocity, setpoint.chassisAcceleration)
            Ramsete -> Drive.updateRamsete(error, setpoint.chassisVelocity)
        }
        val setpointState = setpoint.state.state
        val robotState = Drive.robotState
        val data = arrayOf(
                // "t"
                setpoint.t,
                setpoint.velocity, setpoint.acceleration, setpoint.state.curvature,
                // "s_x", "s_y", "s_theta",
                robotState.translation.x, robotState.translation.y, robotState.rotation.radians,
                // "r_x", "r_y", "r_theta"
                setpointState.translation.x, setpointState.translation.y, setpointState.rotation.radians,
                // "left", "right"
                io.leftFeedforward * 12.0, io.rightFeedforward * 12.0
        )
        logger.writeData(*data)
        //println(data.joinToString("\t") { it.f })
    }

    override val shouldFinish: Boolean get() = Drive.isDoneTrajectory()

    override fun stop() {
        Drive.neutralOutput()
    }
}