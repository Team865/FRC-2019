package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Lift

class PositionOnly : Action {

    var setpoint = 0.0

    override val shouldFinish: Boolean
        get() = super.shouldFinish

    override fun update() {
        LiftMotionPlanner.setSetpoint(setpoint, isMotionPlanningEnabled = false)
        LiftMotionPlanner.compute()
    }

    override fun stop() {
        Lift.set(LiftState.kOpenLoop) { speed = 0.0 }
    }
}