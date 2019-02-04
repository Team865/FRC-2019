package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc.OpenLoopState
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.lift.*


object LiftState {
    val kIdle = runOnce { }
    val kOpenLoop = OpenLoopState { Lift.percentOutput = it }
    val kGoToPosition = GoToPosition

    val kToPositionBase = runOnce {
        when (LiftConstants.kType){
            liftMotionType.LinearPID -> {
                kGoToPosition
            }
            liftMotionType.OptimisedMotion ->{
                kGoToPositionPlanned
            }
            liftMotionType.PlannedMotion ->{
                kGoToPositionOptimisedNotPlanned
            }
        }
    }
    val kGoToPositionPlanned = GoToPositionMotionPlanning
    val kGoToPositionOptimisedNotPlanned = GoToPositionMotionPlanningSimple
    val kHoldPosition = HoldPosition
}