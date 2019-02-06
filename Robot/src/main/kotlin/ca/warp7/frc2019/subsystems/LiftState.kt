package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc.OpenLoopState
import ca.warp7.frc2019.subsystems.lift.*


object LiftState {
    val kIdle = runOnce { }
    val kOpenLoop = OpenLoopState {
        Lift.outputType = Lift.OutputType.Percent
        Lift.demand = it
    }
    val kGoToPosition = GoToPosition // this cal be relaced with motion planning or motion planning simple
    val kGoToPositionPlanned = GoToPositionMotionPlanning
    val kGoToPositionOptimisedNotPlanned = GoToPositionMotionPlanningSimple
    val kHoldPosition = HoldPosition
    val kFollowTrajectory = FollowTrajectory
}