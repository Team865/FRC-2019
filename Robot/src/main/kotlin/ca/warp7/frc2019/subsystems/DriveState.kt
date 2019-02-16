@file:Suppress("unused")

package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.drive.CurvatureDrive
import ca.warp7.frc2019.subsystems.drive.DriveDistance
import ca.warp7.frc2019.subsystems.drive.FollowPath
import ca.warp7.frc2019.subsystems.drive.TurnAngle
import com.ctre.phoenix.motorcontrol.ControlMode

object DriveState {

    val kNeutralMotionState = Drive.runOnce {
        set(kNeutralOutput)
    }

    val kNeutralOutput = Drive.runOnce {
        controlMode = ControlMode.PercentOutput
        leftDemand = 0.0
        rightDemand = 0.0
    }

    val kCurvature = CurvatureDrive

    val kDriveDistance = DriveDistance

    val kTurnAngle = TurnAngle

    val kFollowPath = FollowPath
}
