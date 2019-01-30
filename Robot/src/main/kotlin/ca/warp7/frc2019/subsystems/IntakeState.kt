package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.runOnce

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

    val kOpenLoop = runOnce {}
}