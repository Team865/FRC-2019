package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc.OpenLoopState

object IntakeState {

    val kUp = Intake.runOnce {
        extended = false
        speed = 0.0
    }

    val kExtendedOpenLoop = OpenLoopState {
        Intake.extended = true
        Intake.speed = it
    }
}