package ca.warp7.frc2019.actions.lift.deprecated

import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import kotlin.math.sign
import kotlin.math.sqrt

object LiftTrajectory {
    private val io: BaseIO = ioInstance()
    var relativeTargetHeight = 0.0
    var isTriangle = false
    var maxReachedVelocity = 0.0
    var totalTimeEstimate = 0.0
    var timeUntilMaxVelocityReachedEstimate = 0.0

    fun generateTrajectory(relativeHeight: Double) {
        relativeTargetHeight = relativeHeight
        val dtFromZeroVelocity = io.liftVelocity / LiftConstants.kMaxBaseAcceleration
        val dxFromZeroVelocity = (io.liftVelocity / 2 * dtFromZeroVelocity) * sign(relativeHeight)
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

        totalTimeEstimate = if (isTriangle) {
            2 * timeUntilMaxVelocityReachedEstimate
        } else {
            val dxtomaxV = maxReachedVelocity / 2 * timeUntilMaxVelocityReachedEstimate
            val dxatcruiseV = relativeHeight - dxtomaxV * 2
            val dtatcruiseV = dxatcruiseV / maxReachedVelocity
            val tAcandDc = 2 * timeUntilMaxVelocityReachedEstimate
            dtatcruiseV + tAcandDc
        }
    }

    fun desiredVelocity(timeSinceStart: Double): Double {
        // TODO use position data for feedback loop
        // from Trapezoidal Velocity Drive
        if (timeSinceStart > totalTimeEstimate) {
            return 0.0
        }

        return if (isTriangle) {
            if (timeSinceStart <= timeUntilMaxVelocityReachedEstimate) {
                timeSinceStart * LiftConstants.kMaxBaseAcceleration
            } else {
                maxReachedVelocity - (timeSinceStart - timeUntilMaxVelocityReachedEstimate) * LiftConstants.kMaxBaseAcceleration
            }
        } else {
            when {
                timeSinceStart <= timeUntilMaxVelocityReachedEstimate -> timeSinceStart * LiftConstants.kMaxBaseAcceleration
                timeSinceStart >= totalTimeEstimate - timeUntilMaxVelocityReachedEstimate -> (totalTimeEstimate - timeSinceStart) * LiftConstants.kMaxBaseAcceleration
                else -> maxReachedVelocity
            }
        }
    }
}