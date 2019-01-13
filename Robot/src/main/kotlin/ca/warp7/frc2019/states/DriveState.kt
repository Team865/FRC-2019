@file:Suppress("unused")

package ca.warp7.frc2019.states

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.Drive

object DriveState {

    val Neutral = runOnce {
        Drive.apply {
            outputMode = Drive.OutputMode.Percent
            leftDemand = 0.0
            rightDemand = 0.0
            leftMaster.neutralOutput()
            rightMaster.neutralOutput()
        }
    }

    object Curvature : Action {
        var throttle = 0.0
        var wheel = 0.0
        var quickTurn = false

        override fun start() {
            Drive.outputMode = Drive.OutputMode.WPILibControlled
        }

        override fun update() {
            Drive.differentialDrive.curvatureDrive(throttle, wheel, quickTurn)
        }

        override fun shouldFinish() = false

        override fun stop() {
            Drive.set(Neutral)
        }
    }
}