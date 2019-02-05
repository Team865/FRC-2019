package ca.warp7.frc2019.subsystems.lift.planner

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift

object LiftMotionPlanner {
    val currentHeight get() = Lift.positionTicks / LiftConstants.kInchesPerTick
    val currentVelocity get() = Lift.velocityTicksPer100ms / LiftConstants.kInchesPerTick * 10

    var nominalZero = 0

    fun updateMeasurements() {
        if (Lift.velocityTicksPer100ms < LiftConstants.kStoppedVelocityThreshold
                && Lift.actualCurrent.epsilonEquals(0.0, LiftConstants.kCurrentEpsilon)
                && Lift.hallEffectTriggered) {
            zeroLiftHeight()
        }
    }

    fun zeroLiftHeight() {
        nominalZero = Lift.positionTicks
    }
}