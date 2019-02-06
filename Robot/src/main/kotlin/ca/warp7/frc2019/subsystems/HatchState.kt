package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.actionkt.wait

object HatchState {
    val kIdle = runOnce { Hatch.pushing = false }

    val kPushing = Hatch.runOnce {
        pushing = true
        wait(0.5)
        set(kIdle)
    }
}