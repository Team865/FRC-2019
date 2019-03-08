package ca.warp7.frc2019.subsystems.lift

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.constants.LiftConstants.kTicksPerInch
import ca.warp7.frc2019.subsystems.Lift
import com.ctre.phoenix.motorcontrol.ControlMode
import java.lang.Math.abs
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.sqrt

object LiftMotionPlanner {

    private val measurementFrequency = 1000 / LiftConstants.kMasterTalonConfig.velocityMeasurementPeriod.value
    private val squaredFrequency = measurementFrequency * measurementFrequency

    val height get() = (Lift.positionTicks) / LiftConstants.kTicksPerInch
    val velocity get() = Lift.velocityTicks / LiftConstants.kTicksPerInch * measurementFrequency
    val acceleration get() = accelerationTicksPer100ms2 / LiftConstants.kTicksPerInch * squaredFrequency

    private var motionPlanningEnabled = false
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
        if (!dt.epsilonEquals(0.0, LiftConstants.kEpsilon)) {
            dvBuffer.add(Lift.velocityTicks - previousVelocityTicks)
            dtBuffer.add(dt)
            if (dvBuffer.size > LiftConstants.kAccelerationMeasurementFrames) {
                dvBuffer.removeAt(0)
                dtBuffer.removeAt(0)
            }
            accelerationTicksPer100ms2 = dvBuffer.sum() / dtBuffer.sum()
            previousVelocityTicks = Lift.velocityTicks
        }
    }

    fun setSetpoint(newSetpoint: Double, isMotionPlanningEnabled: Boolean) {
        motionPlanningEnabled = isMotionPlanningEnabled
        //if (newSetpoint < LiftConstants.kHomeHeightInches || newSetpoint > LiftConstants.kMaximumSetpoint) return
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
                    !state.height.epsilonEquals(setpointInches, LiftConstants.kEpsilon)){
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
                feedforward = LiftConstants.kPrimaryFeedforward +
                        (it.height - height) * LiftConstants.kPurePursuitPositionGain
            }
        } else {
            controlMode = ControlMode.Position
            demand = -(setpointInches * LiftConstants.kTicksPerInch)
            feedforward = LiftConstants.kPrimaryFeedforward
        }
    }

    data class LiftMotionState(
            val height: Double,
            val velocity: Double
    )
}