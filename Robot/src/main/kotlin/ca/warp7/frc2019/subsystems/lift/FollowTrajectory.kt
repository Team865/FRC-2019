package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Lift

object FollowTrajectory : Action {

    var setpoint = 0.0

    override fun start() {
        LiftMotionPlanner.setSetpoint(setpoint, isMotionPlanningEnabled = true)
    }

    override val shouldFinish: Boolean
        get() = false

    override fun update() {
        LiftMotionPlanner.compute()
    }

    override fun stop() {
        Lift.set(LiftState.kOpenLoop) {
            speed = 0.0
        }
    }
}