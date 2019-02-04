@file:Suppress("unused")

package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.action
import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.drive.CurvatureDrive

object DriveState {

    val kNeutralOutput = Drive.runOnce {
        outputMode = Drive.OutputMode.Percent
        leftDemand = 0.0
        rightDemand = 0.0
    }

    val kCurvature = CurvatureDrive

    val kLinearPID = action {
        var distance = 0.0


    }

    val kFollowingTrajectory: Action = TODO()
}