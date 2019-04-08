package ca.warp7.frc.drive

data class DynamicState(
        var voltage: WheelState = WheelState(0.0, 0.0),  // V
        var torque: WheelState = WheelState(0.0, 0.0)  // N m
)