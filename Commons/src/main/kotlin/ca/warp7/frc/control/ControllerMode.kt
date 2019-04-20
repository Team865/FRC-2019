package ca.warp7.frc.control

enum class ControllerMode(val value: Int) {
    DriverAndOperator(0),
    DriverOnly(1),
    OperatorOnly(2)
}