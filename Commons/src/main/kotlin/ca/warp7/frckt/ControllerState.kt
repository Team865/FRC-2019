package ca.warp7.frckt

enum class ControllerState {
    Pressed, Released, HeldDown, KeptUp
}

internal fun u(old: ControllerState, _new: Boolean): ControllerState {
    return if (_new)
        if (old == ControllerState.Pressed || old == ControllerState.HeldDown) ControllerState.HeldDown else ControllerState.Pressed
    else if (old == ControllerState.Released || old == ControllerState.KeptUp) ControllerState.KeptUp else ControllerState.Released
}