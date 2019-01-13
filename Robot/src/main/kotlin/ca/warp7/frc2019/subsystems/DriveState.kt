@file:Suppress("unused")

package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce

object DriveState {

    val Brake = runOnce {
        Drive.outputMode = Drive.OutputMode.Percent
        Drive.leftDemand = 0.0
        Drive.rightDemand = 0.0
    }

}