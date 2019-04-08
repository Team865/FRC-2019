package ca.warp7.frc.drive

@Suppress("unused")
data class DifferentialDriveModel(
        val wheelRadius: Double,
        val wheelbaseRadius: Double,
        val maxVelocity: Double,
        val maxAcceleration: Double,
        val maxFreeSpeed: Double,
        val speedPerVolt: Double,
        val torquePerVolt: Double,
        val frictionVolts: Double,
        val linearInertia: Double,
        val angularInertia: Double,
        val maxVolts: Double
) {
    fun solve(state: WheelState) = ChassisState(
            linear = (state.left + state.right) / 2.0,
            angular = (state.right - state.left) / (2 * wheelbaseRadius)
    )

    fun solve(state: ChassisState) = WheelState(
            left = state.linear - state.angular * wheelbaseRadius,
            right = state.linear + state.angular * wheelbaseRadius
    )

    fun signedMaxAtCurvature(curvature: Double, maxVel: Double = maxVelocity) =
            signedMaxAtCurvature(curvature, maxVel, wheelbaseRadius)

    fun solvedMaxAtCurvature(curvature: Double) = solve(signedMaxAtCurvature(curvature))
}