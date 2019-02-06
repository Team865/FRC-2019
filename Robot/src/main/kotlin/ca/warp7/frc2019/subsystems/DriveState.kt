@file:Suppress("unused")

package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.*
import ca.warp7.frc2019.subsystems.drive.*

object DriveState {

    val kNeutralOutput = Drive.runOnce {
        outputMode = Drive.OutputMode.Percent
        leftDemand = 0.0
        rightDemand = 0.0
    }

    val kCurvature = CurvatureDrive

    val kDriveDistance = DriveDistance

    val kTurnAngle = TurnAngle

    val kFollowPath = FollowPath
}
