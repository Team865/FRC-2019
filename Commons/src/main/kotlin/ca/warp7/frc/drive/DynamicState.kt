package ca.warp7.frc.drive

data class DynamicState(
        val voltage: WheelState = WheelState(0.0, 0.0),  // V
        val velocity: WheelState = WheelState(0.0, 0.0)  // m/s
) {
    override fun toString(): String {
        return "(voltage=$voltage, speed=$velocity)"
    }
}