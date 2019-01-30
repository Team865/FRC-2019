@file:Suppress("unused")

package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.drive.CurvatureDrive

object DriveState {

    val kNeutralOutput = runOnce {
        Drive.apply {
            outputMode = Drive.OutputMode.Percent
            leftDemand = 0.0
            rightDemand = 0.0
            leftMaster.neutralOutput()
            rightMaster.neutralOutput()
        }
    }

    val kCurvature = CurvatureDrive
}