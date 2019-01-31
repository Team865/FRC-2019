package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.runOnce
import ca.warp7.frc.OpenLoopState

object IntakeState {
    val kIdle = runOnce {
        Intake.apply {
            victor.neutralOutput()
        }
    }

    val kUp = runOnce {
        Intake.apply {
            solenoid.set(false)
            victor.neutralOutput()
        }
    }

    val kStartOpenLoop: Action = runOnce {
        Intake.apply {
            solenoid.set(true)
            set(IntakeState.kOpenLoop)
        }
    }

    val kOpenLoop = OpenLoopState { Intake.speed = it }
}