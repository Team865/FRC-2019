package ca.warp7.frc2019.subsystems.lift

import ca.warp7.frc2019.constants.LiftConstants
import kotlin.math.abs
import kotlin.math.pow

fun shouldDecelerate(relativeHeight : Double, currentVelocity : Double): Boolean {
    val startDeceletatingAtHeightRelativeToTarget = -1 * currentVelocity.pow(2) / (2 * LiftConstants.kMaxBaseAcceleration)
    return (startDeceletatingAtHeightRelativeToTarget >= abs(relativeHeight))
}