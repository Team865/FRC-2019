package ca.warp7.frc2019.subsystems.lift

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Infrastructure
import ca.warp7.frc2019.subsystems.Lift

@Suppress("unused")
object LiftMotionPlanner {
    val currentHeight get() = (Lift.positionTicks - nominalZero) / LiftConstants.kInchesPerTick
    val currentVelocity get() = Lift.velocityTicksPer100ms / LiftConstants.kInchesPerTick * 10

    var nominalZero = 0
    var setpoint = 0.0

    val setpointTicks get() = setpoint * LiftConstants.kInchesPerTick + nominalZero

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

    fun computePosition() {
        Lift.apply {
            outputType = Lift.OutputType.Position
            demand = setpointTicks
            feedForward = primaryFeedforward()
        }
    }

    private fun primaryFeedforward(): Double {
        var feedforward = 0.0
        if (Infrastructure.calibrated) {
            feedforward *= Math.cos(Infrastructure.pitch)
        }
        return feedforward
    }
}