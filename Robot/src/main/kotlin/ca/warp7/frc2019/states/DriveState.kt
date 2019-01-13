@file:Suppress("unused")

package ca.warp7.frc2019.states

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.states.drive.CurvatureDrive
import ca.warp7.frc2019.subsystems.Drive

object DriveState {

    val NeutralOutput = runOnce {
        Drive.apply {
            outputMode = Drive.OutputMode.Percent
            leftDemand = 0.0
            rightDemand = 0.0
            leftMaster.neutralOutput()
            rightMaster.neutralOutput()
        }
    }

    val Curvature = CurvatureDrive
}