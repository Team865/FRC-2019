package ca.warp7.frc2019.subsystems.superstructure

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Outtake

object ManualOuttake : Action {
    var speed = 0.0

    override fun update() {
        Outtake.speed = speed
    }
}