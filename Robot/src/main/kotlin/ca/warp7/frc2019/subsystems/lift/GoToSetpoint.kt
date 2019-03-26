package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action

class GoToSetpoint(var setpoint: Double = 0.0) : Action {

    override val shouldFinish: Boolean
        get() = false

    override fun update() {
        LiftMotionPlanner.setSetpoint(setpoint, isMotionPlanningEnabled = false)
        LiftMotionPlanner.compute()
    }
}