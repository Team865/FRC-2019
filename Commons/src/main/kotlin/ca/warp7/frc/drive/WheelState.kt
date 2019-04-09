package ca.warp7.frc.drive

data class WheelState(val left: Double, val right: Double) {
    override fun toString(): String {
        return "Wheel(${"%.3f".format(left)}, ${"%.3f".format(right)})"
    }

    operator fun times(by: Double) = WheelState(left * by, right * by)
}