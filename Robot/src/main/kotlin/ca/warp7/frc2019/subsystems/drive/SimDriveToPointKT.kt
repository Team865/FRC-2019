package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.geometry.degrees
import ca.warp7.frc.geometry.invertX
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure

class SimDriveToPointKT(var targetPose: Pose2D) : Action {
    var turnRate = 7.0
    var maxTurn = 25.0

    override fun start() {
    }

    override fun update() {
        val relTargetPose = targetPose - DriveMotionPlanner.robotState
        var translationError = relTargetPose.translation
        var rotationError = relTargetPose.rotation

        if (translationError.y<0) translationError=translationError.invertX()

        val rotationOffset =(translationError.x * turnRate).coerceIn(-maxTurn..maxTurn)

        val targetYaw = Infrastructure.yaw.degrees - (translationError.x * turnRate).coerceIn(-maxTurn..maxTurn);
    }

    override val shouldFinish: Boolean
        get() = super.shouldFinish

    override fun stop() {
        Drive.apply {
            leftDemand = 0.0
            rightDemand = 0.0
        }
    }
}