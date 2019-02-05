package ca.warp7.frc2019.subsystems.lift.planner

import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift

object LiftMotionPlanner {
    val positionInches get() = Lift.positionTicks / LiftConstants.kInchesPerTick
    val velocityInchesPerSecond get() = Lift.velocityTicksPer100ms / LiftConstants.kInchesPerTick * 10

    var nominalZero = 0

    fun updateMeasurements() {
    }

    fun zeroHeight() {
        nominalZero = Lift.positionTicks
    }
}