@file:Suppress("unused")

package ca.warp7.frc.path

fun DifferentialModel.solve(state: WheelState) = ChassisState(
        linear = (state.left + state.right) / 2.0,
        angular = (state.right - state.left) / trackWidth
)

fun DifferentialModel.solve(state: ChassisState) = WheelState(
        left = state.linear - state.angular * trackWidth * 0.5,
        right = state.linear + state.angular * trackWidth * 0.5
)