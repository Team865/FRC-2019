package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.runOnce
import ca.warp7.frc.OpenLoopState

object IntakeState {
    val kIdle = runOnce {
        Intake.apply {
            speed = 0.0
        }
    }

    val kUp = runOnce {
        Intake.apply {
            extended = false
            speed = 0.0
        }
    }

    val kStartOpenLoop: Action = runOnce {
        Intake.apply {
            extended = true
            set(IntakeState.kOpenLoop)
        }
    }

    val kOpenLoop = OpenLoopState { Intake.speed = it }
}