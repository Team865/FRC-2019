package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants.kHomeHeightInches
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.LiftState


object GoToPosition: Action {
    var heightInputAbsoluteInches = 0.0
    var targetHeightFromHome = 0.0

    fun setHeightAbsoluteInches(height : Double) {
        targetHeightFromHome = height - kHomeHeightInches
    }

    override fun update(){
        Lift.demandedHeightFromHome = targetHeightFromHome
    }

    override fun stop() {
        Lift.set(LiftState.kHoldPosition)
    }

}