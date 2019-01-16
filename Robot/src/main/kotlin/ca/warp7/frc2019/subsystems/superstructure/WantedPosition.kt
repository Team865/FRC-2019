package ca.warp7.frc2019.subsystems.superstructure

class WantedPosition {

    enum class SetpointType {
        Cargo, HatchPanel
    }

    enum class PositionType {
        Normal,
        Defending,
        Climbing
    }

    var positionType = PositionType.Normal

    private var setpoint = 0
    var setpointType = SetpointType.Cargo

    fun increaseLiftSetpoint() {
        setpoint++
    }

    fun decreaseLiftSetpoint() {
        setpoint--
    }

    var defending = false
}