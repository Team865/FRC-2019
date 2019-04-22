package ca.warp7.frc2019.actions

import ca.warp7.actionkt.Action
import ca.warp7.frc.drive.WheelState
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.fromRadians
import ca.warp7.frc.geometry.translation
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.subsystems.Drive

class RobotStateEstimator : Action {

    private val io: RobotIO = RobotIO

    override val shouldFinish: Boolean
        get() = false

    override fun update() {

        // convert rad/s into m/s
        val wheelVelocity = WheelState(
                io.leftVelocity * Drive.model.wheelRadius,
                io.rightVelocity * Drive.model.wheelRadius
        )

        // solve into chassis velocity
        Drive.chassisVelocity = Drive.model.solve(wheelVelocity)

        // If gyro connected, use the yaw value from the gyro as the new angle
        // otherwise add the calculated angular velocity to current yaw
        val theta = Drive.robotState.rotation + Rotation2D.fromRadians(io.dt *
                (if (io.gyroConnected) io.angularVelocity else Drive.chassisVelocity.angular))

        // add displacement into current position
        val pos = Drive.robotState.translation + theta.translation * (Drive.chassisVelocity.linear * io.dt)

        // update the robot state
        Drive.robotState = Pose2D(pos, theta)
    }
}