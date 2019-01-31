package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.action
import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.lift.GoToPosition
import ca.warp7.frc2019.subsystems.lift.OpenLoopLift

object LiftState {
    val kIdle = runOnce { }
    val kOpenLoop = OpenLoopLift
    val kGoToPosition = GoToPosition


    val state = action {

        onStart {

        }

        finishWhen {
            true
        }

        onUpdate { }

        onStop { }
    }
}