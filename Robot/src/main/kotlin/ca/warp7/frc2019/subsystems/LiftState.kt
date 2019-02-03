package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc.OpenLoopState
import ca.warp7.frc2019.subsystems.lift.GoToPosition
import ca.warp7.frc2019.subsystems.lift.HoldPosition
import ca.warp7.frc2019.subsystems.lift.OpenLoopLift


object LiftState {
    val kIdle = runOnce { }
    val kOpenLoop = OpenLoopState{Lift.percentOutput = it}
    val kGoToPosition = GoToPosition
    val kHoldPosition = HoldPosition
}