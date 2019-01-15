package ca.warp7.frc2019.states.superstructure

class WantedPosition {

    enum class SetpointType {
        Cargo, HatchPanel
    }

    private var setpoint = 0
    var setpointType = SetpointType.Cargo

    fun increaseLiftSetpoint() {
    }

    fun decreaseLiftSetpoint() {

    }
}