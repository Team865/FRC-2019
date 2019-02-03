package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.DriveState

object CurvatureDrive : Action {
    var xSpeed = 0.0
    var zRotation = 0.0
    var isQuickTurn = false

    override fun start() {
        Drive.outputMode = Drive.OutputMode.WPILibControlled
    }

    override fun update() {
        Drive.doWithCheckedWPIState { curvatureDrive(xSpeed, zRotation, isQuickTurn) }
    }

    override val shouldFinish get() = false

    override fun stop() {
        Drive.set(DriveState.kNeutralOutput)
    }
}