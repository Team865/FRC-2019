@file:Suppress("unused")

package ca.warp7.frc.drive

fun DifferentialDriveModel.solve(state: WheelState) = ChassisState(
        linear = (state.left + state.right) / 2.0,
        angular = (state.right - state.left) / (2 * wheelbaseRadius)
)

fun DifferentialDriveModel.solve(state: ChassisState) = WheelState(
        left = state.linear - state.angular * wheelbaseRadius,
        right = state.linear + state.angular * wheelbaseRadius
)