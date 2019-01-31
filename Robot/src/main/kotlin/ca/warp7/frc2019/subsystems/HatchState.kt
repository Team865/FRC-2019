package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.actionTimer
import ca.warp7.actionkt.runOnce

object HatchState {
    val kIdle = runOnce { Hatch.pushing = false }

    val kPushing = runOnce {
        Hatch.apply {
            pushing = true
            actionTimer { 0.5 }
            set(kIdle)
        }
    }
}