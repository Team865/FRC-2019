package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.lift.planner.LiftMotionPlanner
import java.lang.Math.signum

object GoToPositionMotionPlanningSimple : Action {
    var heightInputAbsoluteInches = 0.0
    var targetHeightFromHome = 0.0

    override fun start() {
        Lift.outputType = Lift.OutputType.Velocity
    }

    override fun update() {
        targetHeightFromHome = heightInputAbsoluteInches - LiftConstants.kHomeHeightInches
        val relativeDistanceToTarget = targetHeightFromHome - LiftMotionPlanner.position
        if (shouldDecelerate(Lift.velocityInchesPerSecond, relativeDistanceToTarget)) {
            Lift.demand = LiftConstants.kMaxVelocityInchesPerSecond * signum(relativeDistanceToTarget)
        } else {
            Lift.demand = 0.0
        }
    }

    override val shouldFinish: Boolean
        get() {
            return LiftMotionPlanner.position == targetHeightFromHome && Lift.velocityInchesPerSecond == Lift.demand
        }
}