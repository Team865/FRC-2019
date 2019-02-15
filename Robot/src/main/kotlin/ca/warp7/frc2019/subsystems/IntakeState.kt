package ca.warp7.frc2019.subsystems

import ca.warp7.frc.OpenLoopState

object IntakeState {
    val kExtendedOpenLoop = OpenLoopState {
        Intake.extended = true
        Intake.speed = it
    }
}