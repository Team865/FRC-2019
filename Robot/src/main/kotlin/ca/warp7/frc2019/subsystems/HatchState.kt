package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.action
import ca.warp7.actionkt.runOnce
import ca.warp7.actionkt.wait

object HatchState {
    val kIdle = runOnce { Hatch.pushing = false }

    val kPushingOld = Hatch.runOnce {
        pushing = true
        wait(0.5)
        set(kIdle)
    }

    val kPushing = action {
        onStart { Hatch.pushing = true }
        finishWhen { elapsed > 0.5 }
        onStop { Hatch.set(HatchState.kIdle) }
    }
}