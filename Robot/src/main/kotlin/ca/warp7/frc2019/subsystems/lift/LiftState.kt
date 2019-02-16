package ca.warp7.frc2019.subsystems.lift

import ca.warp7.frc.OpenLoopState
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift
import com.ctre.phoenix.motorcontrol.ControlMode


object LiftState {

    val kOpenLoop = OpenLoopState {
        Lift.controlMode = ControlMode.PercentOutput
        Lift.demand = it
        Lift.feedforward = LiftConstants.kPrimaryFeedforward
    }

    val kGoToPosition = GoToPosition // this cal be relaced with motion planning or motion planning simple

    val kGoToPositionPlanned = GoToPositionMotionPlanning

    val kGoToPositionOptimisedNotPlanned = GoToPositionMotionPlanningSimple

    val kHoldPosition = HoldPosition

    val kFollowTrajectory = FollowTrajectory
}