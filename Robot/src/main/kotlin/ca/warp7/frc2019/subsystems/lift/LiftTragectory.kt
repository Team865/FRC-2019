package ca.warp7.frc2019.subsystems.lift

import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift
import kotlin.math.sign
import kotlin.math.sqrt

object liftTragectory {
    var relativeTargetHeight = 0.0
    var isTriange = false
    var maxReachedVelocity = 0.0
    var totalTimeEstimate = 0.0
    var timeUntilMaxVelocityReachedEstimate = 0.0

    fun generateTragectory(relativeHeight: Double) {
        relativeTargetHeight = relativeHeight
        val dtFromZeroVelocity = Lift.velocity / LiftConstants.kMaxBaseAcceleration
        val dxFromZeroVelocity = (Lift.velocity / 2 * dtFromZeroVelocity) * sign(relativeHeight)
        val linearChangeAtMaxTheoreticalVelocity = (relativeHeight + dxFromZeroVelocity) / 2
        val maximumTheorecticallyReachableVelocity = sqrt(2 * LiftConstants.kMaxBaseAcceleration * linearChangeAtMaxTheoreticalVelocity)
        val relativeHeightAtMaxTheoreticalVelocity = //TODO / 2
                if (LiftConstants.kMaxVelocityInchesPerSecond >= maximumTheorecticallyReachableVelocity) {
                    isTriange = true
                    maxReachedVelocity = maximumTheorecticallyReachableVelocity
                } else {
                    isTriange = false
                    maxReachedVelocity = LiftConstants.kMaxVelocityInchesPerSecond
                }

        timeUntilMaxVelocityReachedEstimate = maxReachedVelocity / LiftConstants.kMaxBaseAcceleration

        if (isTriange) {
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
        // from Trapezoidal Velocity Drive
        if (timeSinceStart > totalTimeEstimate) {
            return 0.0
        }

        if (isTriange) {
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