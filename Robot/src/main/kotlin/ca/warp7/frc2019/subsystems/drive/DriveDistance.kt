package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.DriveState

object DriveDistance : Action {
    var distance = 0.0

    override fun start() {
        Drive.outputMode = Drive.OutputMode.Position
    }

    override fun update() {
        Drive
    }

    override val shouldFinish get() = false

    override fun stop() {
        Drive.set(DriveState.kNeutralOutput)
    }
}