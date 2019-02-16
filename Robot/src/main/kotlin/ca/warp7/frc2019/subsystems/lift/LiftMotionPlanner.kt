package ca.warp7.frc2019.subsystems.lift

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Infrastructure
import ca.warp7.frc2019.subsystems.Lift
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.sqrt

object LiftMotionPlanner {

    private val measurementFrequency = 1000 / LiftConstants.kMasterTalonConfig.velocityMeasurementPeriod.value
    private val squaredFrequency = measurementFrequency * measurementFrequency

    val height get() = (Lift.positionTicks - nominalZero) / LiftConstants.kInchesPerTick
    val velocity get() = Lift.velocityTicksPer100ms / LiftConstants.kInchesPerTick * measurementFrequency
    val acceleration get() = accelerationTicksPer100ms2 / LiftConstants.kInchesPerTick * squaredFrequency

    private var motionPlanningEnabled = false
    private var nominalZero = 0
    private var previousSetpoint = 0.0
    private var setpointInches = 0.0
    private var previousVelocityTicks = 0
    private var accelerationTicksPer100ms2 = 0.0
    private var vMax = 0.0
    private var startHeight = 0.0
    private var dyTotal = 0.0
    private val dvBuffer = mutableListOf<Int>()
    private val dtBuffer = mutableListOf<Double>()

    fun updateMeasurements(dt: Double) {
        if (Lift.velocityTicksPer100ms < LiftConstants.kStoppedVelocityThreshold
                && Lift.actualCurrent.epsilonEquals(0.0, LiftConstants.kStoppedCurrentEpsilon)
                && Lift.hallEffectTriggered) {
            nominalZero = Lift.positionTicks
        }
        if (!dt.epsilonEquals(0.0, LiftConstants.kEpsilon)) {
            dvBuffer.add(Lift.velocityTicksPer100ms - previousVelocityTicks)
            dtBuffer.add(dt)
            if (dvBuffer.size > LiftConstants.kAccelerationMeasurementFrames) {
                dvBuffer.removeAt(0)
                dtBuffer.removeAt(0)
            }
            accelerationTicksPer100ms2 = dvBuffer.sum() / dtBuffer.sum()
            previousVelocityTicks = Lift.velocityTicksPer100ms
        }
    }

    fun setSetpoint(newSetpoint: Double, isMotionPlanningEnabled: Boolean = false) {
        motionPlanningEnabled = isMotionPlanningEnabled
        if (newSetpoint < 0 || newSetpoint > LiftConstants.kMaximumSetpoint) return
        val adjustedSetpoint = newSetpoint - LiftConstants.kHomeHeightInches
        if (!adjustedSetpoint.epsilonEquals(previousSetpoint, LiftConstants.kEpsilon)) {
            previousSetpoint = setpointInches
            setpointInches = adjustedSetpoint
            if (motionPlanningEnabled) generateTrajectory()
        }
    }

    private fun generateTrajectory() {
        val height = height
        val dyToGo = setpointInches - height
        val dtSinceStart = velocity / LiftConstants.kMaxAcceleration
        val dySinceStart = dyToGo.sign * (0 + velocity) / 2 * dtSinceStart
        startHeight = height - dySinceStart
        dyTotal = dySinceStart + dyToGo
        val maxProfileVelocity = sqrt(LiftConstants.kMaxAcceleration * dyTotal)
        vMax = min(LiftConstants.kMaxVelocityInchesPerSecond, maxProfileVelocity)
    }

    private val currentMotionState get() = LiftMotionState(height, velocity)

    private val nextMotionState: LiftMotionState
        get() {
            val state = currentMotionState
            if (state.height !in startHeight..setpointInches
                    && !state.height.epsilonEquals(setpointInches, LiftConstants.kEpsilon)) generateTrajectory()
            val nextDt = dtBuffer.average()
            val sign = dyTotal.sign
            val v1 = sqrt(2 * state.height * LiftConstants.kMaxAcceleration * sign)
            val v2 = sqrt(2 * (state.height - setpointInches) * LiftConstants.kMaxAcceleration * sign)
            val nextVelocity = when {
                v1 < vMax && v1 < v2 -> state.velocity + nextDt * LiftConstants.kMaxAcceleration * sign
                v2 < vMax && v2 < v1 -> state.velocity - nextDt * LiftConstants.kMaxAcceleration * sign
                vMax < v1 && vMax < v2 -> vMax
                else -> 0.0
            }
            val nextPosition = state.height + nextDt * nextVelocity
            return LiftMotionState(nextPosition, nextVelocity)
        }

    private val baseFeedForward: Double
        get() {
            var feedforward = LiftConstants.kPrimaryFeedforward
            if (Infrastructure.ahrsCalibrated) feedforward *= Math.cos(Infrastructure.pitch)
            return feedforward
        }

    fun compute() = Lift.apply {
        if (motionPlanningEnabled) {
            nextMotionState.let {
                outputType = Lift.OutputType.Velocity
                demand = it.velocity
                feedForward = baseFeedForward + (it.height - height) * LiftConstants.kPurePursuitPositionGain
            }
        } else {
            outputType = Lift.OutputType.Position
            demand = setpointInches * LiftConstants.kInchesPerTick + nominalZero
            feedForward = baseFeedForward
        }
    }


    data class LiftMotionState(
            val height: Double,
            val velocity: Double
    )
}