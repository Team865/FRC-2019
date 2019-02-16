package ca.warp7.frc2019.subsystems.superstructure

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.lift.LiftMotionPlanner

object PassThrough : Action {

    const val forward = 1.0
    const val reverse = -1.0
    var speed = 0.0
    var outtaking = false

    override fun start() {
        LiftMotionPlanner.height
    }
}