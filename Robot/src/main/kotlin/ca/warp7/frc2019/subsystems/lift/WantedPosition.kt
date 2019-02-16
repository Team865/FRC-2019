package ca.warp7.frc2019.subsystems.lift

import kotlin.math.max
import kotlin.math.min


@Suppress("unused")
class WantedPosition {

    private var setpointLevel = 0
    var setpointType = LiftSetpointType.Cargo

    fun increaseLiftSetpoint() {
        setpointLevel = min(setpointLevel + 1, 3)
    }

    fun decreaseLiftSetpoint() {
        setpointLevel = max(setpointLevel - 1, 0)
    }

    fun toWantedLiftHeight(): Double { // TODO
        return setpointLevel * 5.0 + when (setpointType) {
            LiftSetpointType.Cargo -> 3.0
            LiftSetpointType.Hatch -> 2.0
        }
    }
}