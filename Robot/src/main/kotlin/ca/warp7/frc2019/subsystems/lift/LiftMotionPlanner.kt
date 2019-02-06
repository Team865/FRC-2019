package ca.warp7.frc2019.subsystems.lift

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Infrastructure
import ca.warp7.frc2019.subsystems.Lift
import kotlin.math.sign

@Suppress("unused")
object LiftMotionPlanner {

    private const val kInchesPerTick = LiftConstants.kInchesPerTick
    private val measurementFrequency = 1000 / LiftConstants.kMasterTalonConfig.velocityMeasurementPeriod.value
    private val squaredFrequency = measurementFrequency * measurementFrequency

    val height get() = (Lift.positionTicks - nominalZero) / kInchesPerTick
    val velocity get() = Lift.velocityTicksPer100ms / kInchesPerTick * measurementFrequency
    val acceleration get() = accelerationTicksPer100ms2 / kInchesPerTick * squaredFrequency

    private var motionPlanningEnabled = false
    private var nominalZero = 0
    private var previousSetpoint = 0.0
    private var setpoint = 0.0
    private var previousVelocity = 0
    private var accelerationTicksPer100ms2 = 0.0
    private val dvBuffer = mutableListOf<Int>()
    private val dtBuffer = mutableListOf<Double>()

    fun updateMeasurements(dt: Double) {
        if (Lift.velocityTicksPer100ms < LiftConstants.kStoppedVelocityThreshold
                && Lift.actualCurrent.epsilonEquals(0.0, LiftConstants.kStoppedCurrentEpsilon)
                && Lift.hallEffectTriggered) {
            nominalZero = Lift.positionTicks
        }
        if (!dt.epsilonEquals(0.0, LiftConstants.kEpsilon)) {
            dvBuffer.add(Lift.velocityTicksPer100ms - previousVelocity)
            dtBuffer.add(dt)
            if (dvBuffer.size > LiftConstants.kAccelerationMeasurementFrames) {
                dvBuffer.removeAt(0)
                dtBuffer.removeAt(0)
            }
            accelerationTicksPer100ms2 = dvBuffer.sum() / dtBuffer.sum()
            previousVelocity = Lift.velocityTicksPer100ms
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
                val dyToGo = setpoint - height
                val dtSinceStart = velocity / LiftConstants.kMaxAcceleration
                val dySinceStart = sign(dyToGo) * (0/*Vi*/ + velocity/*Vf*/) / 2 * dtSinceStart
                val dyTotal = dySinceStart + dyToGo
                val dyForMaxVelocity = dyTotal / 2
            }
        }
    }

    private val nextMotionState: LiftMotionState
        get() = LiftMotionState(0.0, 0.0)

    private val nextAdjustedMotionState
        get() = nextMotionState.let {
            LiftMotionState(
                    it.position * kInchesPerTick + nominalZero,
                    it.velocity * kInchesPerTick / measurementFrequency)
        }

    private val baseFeedForward: Double
        get() {
            var feedforward = LiftConstants.kPrimaryFeedforward
            if (height > LiftConstants.kSecondaryStageLiftedSetpoint) {
                feedforward += LiftConstants.kSecondaryStageFeedforward
            }
            if (Infrastructure.calibrated) {
                feedforward *= Math.cos(Infrastructure.pitch)
            }
            return feedforward
        }

    fun compute() = Lift.apply {
        if (motionPlanningEnabled) {
            val state = nextAdjustedMotionState
            outputType = Lift.OutputType.Velocity
            demand = state.velocity
            feedForward = baseFeedForward + (state.position - height) * LiftConstants.kPurePursuitPositionGain
        } else {
            outputType = Lift.OutputType.Position
            demand = setpoint * kInchesPerTick + nominalZero
            feedForward = baseFeedForward
        }
    }


    data class LiftMotionState(
            val position: Double,
            val velocity: Double
    )
}