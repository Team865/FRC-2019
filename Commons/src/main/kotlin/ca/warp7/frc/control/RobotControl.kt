package ca.warp7.frc.control

import ca.warp7.actionkt.Action

object RobotControl : Subsystem() {
    var mode: ControllerMode = ControllerMode.DriverAndOperator
        set(value) {
            InternalControl.setControllerMode(value)
            field = value
        }

    fun enable(wantedState: Action) {
        set(wantedState)
    }

    override fun <T : Action> set(wantedState: T, block: T.() -> Unit) {
        InternalControl.enable()
        super.set(wantedState, block)
    }
}