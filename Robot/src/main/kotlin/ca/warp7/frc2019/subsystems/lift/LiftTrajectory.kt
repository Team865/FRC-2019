package ca.warp7.frc2019.subsystems.lift

import ca.warp7.frc2019.constants.LiftConstants
import kotlin.math.sign
import kotlin.math.sqrt

object LiftTrajectory {
    var relativeTargetHeight = 0.0
    var isTriangle = false
    var maxReachedVelocity = 0.0
    var totalTimeEstimate = 0.0
    var timeUntilMaxVelocityReachedEstimate = 0.0

    fun generateTrajectory(relativeHeight: Double) {
        relativeTargetHeight = relativeHeight
        val dtFromZeroVelocity = LiftMotionPlanner.currentVelocity / LiftConstants.kMaxBaseAcceleration
        val dxFromZeroVelocity = (LiftMotionPlanner.currentVelocity / 2 * dtFromZeroVelocity) * sign(relativeHeight)
        val linearChangeAtMaxTheoreticalVelocity = (relativeHeight + dxFromZeroVelocity) / 2
        val maxTheoreticallyReachableVelocity = sqrt(2 * LiftConstants.kMaxBaseAcceleration * linearChangeAtMaxTheoreticalVelocity) * sign(relativeHeight)
        if (LiftConstants.kMaxVelocityInchesPerSecond >= maxTheoreticallyReachableVelocity) {
            isTriangle = true
            maxReachedVelocity = maxTheoreticallyReachableVelocity
        } else {
            isTriangle = false
            maxReachedVelocity = LiftConstants.kMaxVelocityInchesPerSecond
        }

        timeUntilMaxVelocityReachedEstimate = maxReachedVelocity / LiftConstants.kMaxBaseAcceleration

        if (isTriangle) {
            totalTimeEstimate = 2 * timeUntilMaxVelocityReachedEstimate
        } else {
            val dxtomaxV = maxReachedVelocity / 2 * timeUntilMaxVelocityReachedEstimate
            val dxatcruiseV = relativeHeight - dxtomaxV * 2
            val dtatcruiseV = dxatcruiseV / maxReachedVelocity
            val tAcandDc = 2 * timeUntilMaxVelocityReachedEstimate
            totalTimeEstimate = dtatcruiseV + tAcandDc
        }
    }

    fun desiredVelocoity(timeSinceStart: Double): Double {
        // TODO use position data for feedback loop
        // from Trapezoidal Velocity Drive
        if (timeSinceStart > totalTimeEstimate) {
            return 0.0
        }

        if (isTriangle) {
            if (timeSinceStart <= timeUntilMaxVelocityReachedEstimate) {
                return timeSinceStart * LiftConstants.kMaxBaseAcceleration
            } else {
                return maxReachedVelocity - (timeSinceStart - timeUntilMaxVelocityReachedEstimate) * LiftConstants.kMaxBaseAcceleration
            }
        } else {
            if (timeSinceStart <= timeUntilMaxVelocityReachedEstimate) {
                return timeSinceStart * LiftConstants.kMaxBaseAcceleration
            } else if (timeSinceStart >= totalTimeEstimate - timeUntilMaxVelocityReachedEstimate) {
                return (totalTimeEstimate - timeSinceStart) * LiftConstants.kMaxBaseAcceleration
            } else {
                return maxReachedVelocity
            }
        }
    }
}