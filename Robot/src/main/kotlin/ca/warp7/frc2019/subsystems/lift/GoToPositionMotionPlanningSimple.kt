package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift
import java.lang.Math.signum

object GoToPositionMotionPlanningSimple : Action {
    var heightInputAbsoluteInches = 0.0
    var targetHeightFromHome = 0.0

    override fun update() {
        targetHeightFromHome = heightInputAbsoluteInches - LiftConstants.kHomeHeightInches
        val relativeDistanceToTarget = targetHeightFromHome - Lift.currentPositionFromHome
        if (shouldDecelerate(Lift.currentVelocity, relativeDistanceToTarget)) {
            Lift.demandedVelocity = LiftConstants.kMaxVelocityInchesPerSecond * signum(relativeDistanceToTarget)
        } else {
            Lift.demandedVelocity = 0.0
        }
    }

    override val shouldFinish: Boolean
        get() {
            return Lift.currentPositionFromHome == targetHeightFromHome && Lift.currentVelocity == Lift.demandedVelocity
        }
}