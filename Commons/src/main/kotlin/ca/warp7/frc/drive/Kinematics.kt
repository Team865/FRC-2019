@file:Suppress("unused")

package ca.warp7.frc.drive

import ca.warp7.frc.epsilonEquals
import kotlin.math.abs
import kotlin.math.withSign

fun DifferentialDriveModel.solve(state: WheelState) = ChassisState(
        linear = (state.left + state.right) / 2.0,
        angular = (state.right - state.left) / (2 * wheelbaseRadius)
)

fun DifferentialDriveModel.solve(state: ChassisState) = WheelState(
        left = state.linear - state.angular * wheelbaseRadius,
        right = state.linear + state.angular * wheelbaseRadius
)

/**
 * Calculates the maximum reachable linear and angular velocity based on the curvature.
 * The result is unsigned.
 *
 * The equations are derived from `w(r + L / 2) = far side velocity`. Assume far side goes 100%,
 * we replace it with max velocity and isolate for angular velocity.
 *
 * Then we rearrange `w = (right - left)/L` into `left = maxV - wL`, substitute it into
 * `v = (left + right) / 2`, and get the equation for max linear velocity
 *
 * L is double of wheelBaseRadius, so calculations are simplified here.
 *
 * If curvature is 0, we just return a ChassisState with no angular velocity
 */
fun DifferentialDriveModel.maxAtCurvature(curvature: Double): ChassisState {
    if (curvature == 0.0) return ChassisState(maxVelocity, angular = 0.0)
    val angular = maxVelocity / (1 / curvature + wheelbaseRadius)
    val linear = maxVelocity - (angular * wheelbaseRadius)
    return ChassisState(linear, angular)
}

fun DifferentialDriveModel.solvedMaxAtCurvature(curvature: Double): WheelState {
    if (curvature.epsilonEquals(0.0, 1E-9)) return WheelState(maxVelocity, maxVelocity)
    val max = maxAtCurvature(abs(curvature))
    return solve(ChassisState(max.linear, max.angular.withSign(curvature)))
}