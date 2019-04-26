package ca.warp7.frc2019.actions

import ca.warp7.actionkt.Action
import ca.warp7.frc.CSVLogger
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.radians
import ca.warp7.frc2019.constants.FollowerType
import ca.warp7.frc2019.constants.FollowerType.*
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive
import kotlin.math.abs

class DriveTrajectory(
        val waypoints: Array<Pose2D>,
        val vRatio: Double = 0.8,
        val aRatio: Double = 0.8,
        val backwards: Boolean = false,
        val insertRobotState: Boolean = false,
        val resetInitialState: Boolean = true,
        val followerType: FollowerType = VoltageOnly
) : Action {

    constructor(distance: Double) : this(
            arrayOf(Pose2D.identity, Pose2D(abs(distance), 0.0, Rotation2D.identity)),
            backwards = distance < 0,
            followerType = AnglePID
    )

    private val io: BaseIO = ioInstance()

    private val logger: CSVLogger = io.getLogger("DriveTrajectory")
            .withHeaders(
                    "t",
                    "s_x", "s_y", "s_theta",
                    "r_x", "r_y", "r_theta",
                    "left", "right",
                    "v", "w")

    override fun start() {
        Drive.initTrajectory(waypoints, vRatio, aRatio, backwards, insertRobotState, resetInitialState)
    }

    override fun update() {
        val setpoint = Drive.advanceTrajectory(io.dt)
        val error = Drive.getError(setpoint.state.state)
        when (followerType) {
            VoltageOnly -> Drive.setFeedforward(setpoint.chassisVelocity, setpoint.chassisAcceleration)
            SpeedDemand -> Drive.setDynamicState(setpoint.chassisVelocity, setpoint.chassisAcceleration)
            PosePID -> Drive.updatePosePID(error, setpoint.chassisVelocity, setpoint.chassisAcceleration)
            AnglePID -> Drive.updateAnglePID(setpoint.chassisVelocity, setpoint.chassisAcceleration)
            Ramsete -> Drive.updateRamsete(error, setpoint.chassisVelocity)
        }
        val setpointState = setpoint.state.state
        val robotState = Drive.robotState
        logger.writeData(
                // "t"
                setpoint.t,
                // "s_x", "s_y", "s_theta",
                setpointState.translation.x, setpointState.translation.y, setpointState.rotation.radians,
                // "r_x", "r_y", "r_theta"
                robotState.translation.x, robotState.translation.y, robotState.rotation.radians,
                // "left", "right"
                io.leftFeedforward * 12.0, io.rightFeedforward * 12.0,
                // "v", "w"
                Drive.chassisVelocity.linear, Drive.chassisVelocity.angular
        )
    }

    override val shouldFinish: Boolean get() = Drive.isDoneTrajectory()

    override fun stop() {
        Drive.neutralOutput()
    }
}