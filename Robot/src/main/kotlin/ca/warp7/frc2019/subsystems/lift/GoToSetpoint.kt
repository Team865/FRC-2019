package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift

class GoToSetpoint(var setpoint: Double = 0.0) : Action {

    override val shouldFinish: Boolean
        get() = false

    override fun update() {
        LiftMotionPlanner.setSetpoint(setpoint, isMotionPlanningEnabled = false)
        LiftMotionPlanner.compute()
    }

    override fun stop() {
        Lift.demand = LiftMotionPlanner.setpointInches*LiftConstants.kTicksPerInch
        Lift.feedforward=LiftConstants.kPrimaryFeedforward
    }
}