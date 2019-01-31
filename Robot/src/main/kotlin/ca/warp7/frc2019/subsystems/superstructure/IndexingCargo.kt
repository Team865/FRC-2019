package ca.warp7.frc2019.subsystems.superstructure

import ca.warp7.actionkt.Action

object IndexingCargo : Action {
    var speedScale = 0.0
    var isOverride = false
    private var overrideScale = 0.0

    fun setOverride(scale: Double) {
        isOverride = true
        overrideScale = scale
    }
}