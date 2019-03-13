@file:Suppress("unused")

package ca.warp7.frc.drive

fun DifferentialModel.solve(state: WheelState) = ChassisState(
        linear = (state.left + state.right) / 2.0,
        angular = (state.right - state.left) / (2 * wheelBaseRadius)
)

fun DifferentialModel.solve(state: ChassisState) = WheelState(
        left = state.linear - state.angular * wheelBaseRadius,
        right = state.linear + state.angular * wheelBaseRadius
)