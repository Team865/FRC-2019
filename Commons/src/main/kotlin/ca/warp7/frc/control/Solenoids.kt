package ca.warp7.frc.control

import edu.wpi.first.wpilibj.Solenoid

private class LazySolenoid(channel: Int) : Solenoid(channel) {
    var previousOn = false
    var initialized = false
    override fun set(on: Boolean) {
        if (!initialized) {
            initialized = true
            previousOn = on
            super.set(on)
        } else if (on != previousOn) {
            previousOn = on
            super.set(on)
        }
    }
}

fun lazySolenoid(channel: Int): Solenoid = LazySolenoid(channel)