package ca.warp7.frc2019.subsystems.lift

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Infrastructure
import ca.warp7.frc2019.subsystems.Lift

@Suppress("unused")
object LiftMotionPlanner {
    val currentHeight get() = (Lift.positionTicks - nominalZero) / LiftConstants.kInchesPerTick
    val currentVelocity get() = Lift.velocityTicksPer100ms / LiftConstants.kInchesPerTick * 10

    private var nominalZero = 0
    private var previousSetpoint = 0.0
    private var setpoint = 0.0

    private val setpointTicks get() = setpoint * LiftConstants.kInchesPerTick + nominalZero

    fun setSetpoint(newSetpoint: Double) {
        if (!newSetpoint.epsilonEquals(previousSetpoint, LiftConstants.kEpsilon)) {
            previousSetpoint = setpoint
            setpoint = newSetpoint
        }
    }

    fun updateMeasurements() {
        if (Lift.velocityTicksPer100ms < LiftConstants.kStoppedVelocityThreshold
                && Lift.actualCurrent.epsilonEquals(0.0, LiftConstants.kStoppedCurrentEpsilon)
                && Lift.hallEffectTriggered) {
            nominalZero = Lift.positionTicks
        }
    }

    fun computePositionPID() {
        Lift.apply {
            outputType = Lift.OutputType.Position
            demand = setpointTicks
            feedForward = primaryFeedforward()
        }
    }

    fun computePurePursuitVelocity() {
        Lift.apply {
            outputType = Lift.OutputType.Velocity
            demand = 0.0
            feedForward = primaryFeedforward()
        }
    }

    private fun primaryFeedforward(): Double {
        var feedforward = LiftConstants.kPrimaryFeedforward
        if (currentHeight > LiftConstants.kSecondaryStageLiftedSetpoint) {
            feedforward += LiftConstants.kSecondaryStageFeedforward
        }
        if (Infrastructure.calibrated) {
            feedforward *= Math.cos(Infrastructure.pitch)
        }
        return feedforward
    }
}