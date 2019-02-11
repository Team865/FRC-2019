package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.action
import ca.warp7.actionkt.runOnce

object HatchState {
    val kIdle = runOnce { Hatch.pushing = false }

    val kPushing = action {
        onStart { Hatch.pushing = true }
        finishWhen { elapsed > 0.5 }
        onStop { Hatch.set(HatchState.kIdle) }
    }
}