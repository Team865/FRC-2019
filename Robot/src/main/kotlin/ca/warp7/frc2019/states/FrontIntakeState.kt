package ca.warp7.frc2019.states

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.states.superstructure.ManualFrontIntake

object FrontIntakeState {
    val Idle = runOnce { }

    val ManualControl = ManualFrontIntake
}