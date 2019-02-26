package ca.warp7.frc

import ca.warp7.actionkt.Action

object RobotControl : Subsystem() {
    var mode: ControllerMode = ControllerMode.DriverAndOperator
        set(value) {
            CommonRobot.setControllerMode(value)
            field = value
        }

    fun enable(wantedState: Action) {
        CommonRobot.enable()
        set(wantedState)
    }
}