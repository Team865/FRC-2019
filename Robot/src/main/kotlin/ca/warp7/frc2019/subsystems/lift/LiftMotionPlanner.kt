package ca.warp7.frc2019.subsystems.lift

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.HatchCargo
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.constants.LiftConstants.kTicksPerInch
import ca.warp7.frc2019.subsystems.Lift
import com.ctre.phoenix.motorcontrol.ControlMode
import java.lang.Math.abs
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.sqrt

object LiftMotionPlanner {

    var setpointLevel = 0
    var setpointType = HatchCargo.Hatch

    fun getCoolSetpoint(): Double = when (setpointLevel) {
        0 -> when (setpointType) {
            HatchCargo.Hatch -> FieldConstants.kHatch1Height - LiftConstants.kHatchIntakeHeight
            HatchCargo.Cargo -> FieldConstants.kCargo1Height
        }
        1 -> when (setpointType) {
            HatchCargo.Hatch -> FieldConstants.kHatch2Height - LiftConstants.kHatchIntakeHeight
            HatchCargo.Cargo -> FieldConstants.kCargo2Height
        }
        2 -> when (setpointType) {
            HatchCargo.Hatch -> FieldConstants.kHatch3Height - LiftConstants.kHatchIntakeHeight
            HatchCargo.Cargo -> FieldConstants.kCargo3Height
        }
        else -> LiftConstants.kHomeHeightInches
    }

    fun increaseSetpoint() {
        setpointLevel = (setpointLevel + 1).coerceAtMost(2)
    }

    fun decreaseSetpoint() {
        setpointLevel = (setpointLevel - 1).coerceAtLeast(0)
    }

    private var nominalZero = 0
    val adjustedPositionTicks get() = Lift.position - nominalZero

    private val measurementFrequency = 1000 / LiftConstants.kMasterTalonConfig.velocityMeasurementPeriod.value
    private val squaredFrequency = measurementFrequency * measurementFrequency

    val height get() = (adjustedPositionTicks) / LiftConstants.kTicksPerInch
    val velocity get() = Lift.velocity / LiftConstants.kTicksPerInch * measurementFrequency
    val acceleration get() = accelerationTicksPer100ms2 / LiftConstants.kTicksPerInch * squaredFrequency

    private var motionPlanningEnabled = false
    private var previousSetpoint = 0.0
    var setpointInches = 0.0
    private var previousVelocityTicks = 0
    private var accelerationTicksPer100ms2 = 0.0
    private var vMax = 0.0
    private var startHeight = 0.0
    private var dyTotal = 0.0
    private val dvBuffer = mutableListOf<Int>()
    private val dtBuffer = mutableListOf<Double>()

    fun updateMeasurements(dt: Double) {
        if (!dt.epsilonEquals(0.0, LiftConstants.kEpsilon)) {
            dvBuffer.add(Lift.velocity - previousVelocityTicks)
            dtBuffer.add(dt)
            if (dvBuffer.size > LiftConstants.kAccelerationMeasurementFrames) {
                dvBuffer.removeAt(0)
                dtBuffer.removeAt(0)
            }
            accelerationTicksPer100ms2 = dvBuffer.sum() / dtBuffer.sum()
            previousVelocityTicks = Lift.velocity
        }
        if (Lift.hallEffectTriggered) nominalZero = Lift.position
    }

    fun setSetpoint(newSetpoint: Double, isMotionPlanningEnabled: Boolean) {
        motionPlanningEnabled = isMotionPlanningEnabled
        val adjustedSetpoint = newSetpoint - LiftConstants.kHomeHeightInches
        if (!adjustedSetpoint.epsilonEquals(previousSetpoint, LiftConstants.kEpsilon)) {
            setpointInches = adjustedSetpoint
            previousSetpoint = setpointInches
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
            if (state.height !in startHeight..setpointInches &&
                    !state.height.epsilonEquals(setpointInches, LiftConstants.kEpsilon)) {
                generateTrajectory()
            }
            val nextDt = dtBuffer.average()
            val sign = dyTotal.sign
            val v1 = sqrt(abs(2 * state.height * LiftConstants.kMaxAcceleration)) * sign
            val v2 = sqrt(abs(2 * (state.height - setpointInches) * LiftConstants.kMaxAcceleration)) * sign
            val nextVelocity = when {
                v1 < vMax && v1 < v2 -> state.velocity + nextDt * LiftConstants.kMaxAcceleration * sign
                v2 < vMax && v2 < v1 -> state.velocity - nextDt * LiftConstants.kMaxAcceleration * sign
                vMax < v1 && vMax < v2 -> vMax
                else -> 0.0
            }
            val nextPosition = state.height + nextDt * nextVelocity
            return LiftMotionState(nextPosition, nextVelocity)
        }

    fun compute() = Lift.apply {
        if (motionPlanningEnabled) {
            nextMotionState.let {
                controlMode = ControlMode.Velocity
                demand = kTicksPerInch * it.velocity
                val positionGain = (it.height - height) * LiftConstants.kPurePursuitPositionGain
                feedforward = LiftConstants.kPrimaryFeedforward + positionGain
            }
        } else {
            if (setpointInches < LiftConstants.kPIDDeadSpotHeight || height < LiftConstants.kPIDDeadSpotHeight || hallEffectTriggered) {
                controlMode = ControlMode.Position
                demand = -(setpointInches * LiftConstants.kTicksPerInch) + nominalZero
                feedforward = LiftConstants.kPrimaryFeedforward
            } else {
                // In Dead spot, apply power to go down so that the hall effect sensor can zero the encoder
                controlMode = ControlMode.PercentOutput
                demand = LiftConstants.kMoveToBottomDemand
                feedforward = 0.0
            }
        }
    }

    data class LiftMotionState(
            val height: Double,
            val velocity: Double
    )
}