package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.runOnce
import ca.warp7.frc.OpenLoopState
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.lift.GoToPosition
import ca.warp7.frc2019.subsystems.lift.GoToPositionMotionPlanning
import ca.warp7.frc2019.subsystems.lift.HoldPosition


object LiftState {
    val kIdle = runOnce { }
    val kOpenLoop = OpenLoopState{Lift.percentOutput = it}
    val kGoToPosition = goToPositionState()
    val kHoldPosition = HoldPosition

    private fun goToPositionState () {
        when (LiftConstants.kUsesMotionPlanning){
            true -> GoToPositionMotionPlanning
            false -> GoToPosition
        }
    }
}

