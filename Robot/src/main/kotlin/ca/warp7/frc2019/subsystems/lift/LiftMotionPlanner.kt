package ca.warp7.frc2019.subsystems.lift

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Infrastructure
import ca.warp7.frc2019.subsystems.Lift

@Suppress("unused")
object LiftMotionPlanner {

    private const val kInchesPerTick = LiftConstants.kInchesPerTick
    private val measurementFrequency = 1000 / LiftConstants.kMasterTalonConfig.velocityMeasurementPeriod.value
    private val squaredFrequency = measurementFrequency * measurementFrequency

    val currentHeight get() = (Lift.positionTicks - nominalZero) / kInchesPerTick
    val currentVelocity get() = Lift.velocityTicksPer100ms / kInchesPerTick * measurementFrequency
    val currentAcceleration get() = acceleration / kInchesPerTick * squaredFrequency

    private var motionPlanningEnabled = false
    private var nominalZero = 0
    private var previousSetpoint = 0.0
    private var setpoint = 0.0
    private var previousVelocity = 0
    private var acceleration = 0.0
    private val dvBuffer = mutableListOf<Int>()
    private val dtBuffer = mutableListOf<Double>()
    private val setpointTicks get() = setpoint * kInchesPerTick + nominalZero

    fun updateMeasurements(dt: Double) {
        if (Lift.velocityTicksPer100ms < LiftConstants.kStoppedVelocityThreshold
                && Lift.actualCurrent.epsilonEquals(0.0, LiftConstants.kStoppedCurrentEpsilon)
                && Lift.hallEffectTriggered) {
            nominalZero = Lift.positionTicks
        }
        val dv = Lift.velocityTicksPer100ms - previousVelocity
        previousVelocity = Lift.velocityTicksPer100ms
        if (!dt.epsilonEquals(0.0, LiftConstants.kEpsilon)) {
            dvBuffer.add(dv)
            dtBuffer.add(dt)
            if (dvBuffer.size > LiftConstants.kAccelerationMeasurementFrames) {
                dvBuffer.removeAt(0)
                dtBuffer.removeAt(0)
            }
            acceleration = dvBuffer.sum() / dtBuffer.sum()
        }
    }

    fun setSetpoint(newSetpoint: Double, isMotionPlanningEnabled: Boolean = false) {
        motionPlanningEnabled = isMotionPlanningEnabled
        if (newSetpoint < 0 || newSetpoint > LiftConstants.kMaximumSetpoint) return
        val adjustedSetpoint = newSetpoint - LiftConstants.kHomeHeightInches
        if (!adjustedSetpoint.epsilonEquals(previousSetpoint, LiftConstants.kEpsilon)) {
            previousSetpoint = setpoint
            setpoint = adjustedSetpoint
            if (motionPlanningEnabled) {
            }
        }
    }

    private val nextMotionState: LiftMotionState
        get() = LiftMotionState(0.0, 0.0)

    private val nextAdjustedMotionState
        get() = nextMotionState.let {
            LiftMotionState(
                    it.position * kInchesPerTick + nominalZero,
                    it.velocity * kInchesPerTick / 10)
        }

    fun compute() {
        Lift.apply {
            if (motionPlanningEnabled) {
                val state = nextAdjustedMotionState
                outputType = Lift.OutputType.Velocity
                demand = state.velocity
                val error = state.position - currentHeight
                feedForward = primaryFeedforward() + error * LiftConstants.kPurePursuitPositionGain
            } else {

                outputType = Lift.OutputType.Position
                demand = setpointTicks
                feedForward = primaryFeedforward()
            }
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