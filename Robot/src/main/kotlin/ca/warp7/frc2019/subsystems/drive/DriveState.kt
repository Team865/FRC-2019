@file:Suppress("unused")

package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode

object DriveState {

    val kNeutralOutput = Drive.runOnce {
        controlMode = ControlMode.PercentOutput
        leftDemand = 0.0
        rightDemand = 0.0
    }

    val kCurvature = CurvatureDrive

    val kDriveDistance = DriveDistance

    val kTurnAngle = TurnAngle

    val kFollowPath = FollowPath

    val kTurnPID = TurnToTarget

}
