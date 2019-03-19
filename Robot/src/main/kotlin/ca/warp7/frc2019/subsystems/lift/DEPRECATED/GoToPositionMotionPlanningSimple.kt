package ca.warp7.frc2019.subsystems.lift.DEPRECATED

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.lift.LiftMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import java.lang.Math.signum

object GoToPositionMotionPlanningSimple : Action {
    var heightInputAbsoluteInches = 0.0
    var targetHeightFromHome = 0.0

    override fun start() {
        Lift.controlMode = ControlMode.Velocity
    }

    override fun update() {
        targetHeightFromHome = heightInputAbsoluteInches - LiftConstants.kHomeHeightInches
        val relativeDistanceToTarget = targetHeightFromHome - LiftMotionPlanner.height
        if (shouldDecelerate(LiftMotionPlanner.velocity, relativeDistanceToTarget)) {
            Lift.demand = LiftConstants.kMaxVelocityInchesPerSecond * signum(relativeDistanceToTarget)
        } else {
            Lift.demand = 0.0
        }
    }

    override val shouldFinish: Boolean
        get() {
            return LiftMotionPlanner.height == targetHeightFromHome && LiftMotionPlanner.velocity == Lift.demand
        }
}