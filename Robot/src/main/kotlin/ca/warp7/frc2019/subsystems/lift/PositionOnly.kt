package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action

class PositionOnly : Action {

    var setpoint = 0.0

    override val shouldFinish: Boolean
        get() = false

    override fun update() {
        LiftMotionPlanner.setSetpoint(setpoint, isMotionPlanningEnabled = false)
        LiftMotionPlanner.compute()
    }
}