package ca.warp7.frc.drive

data class DynamicState(
        var voltage: WheelState = WheelState(0.0, 0.0),  // V
        var velocity: WheelState = WheelState(0.0, 0.0)  // m/s
) {
    override fun toString(): String {
        return "(voltage=$voltage, speed=$velocity)"
    }
}