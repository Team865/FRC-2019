package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants.kHomeHeightInches
import ca.warp7.frc2019.subsystems.Lift


object GoToPosition: Action {
    var heightInputAbsoluteInches = 0.0
    var heightFromHome = 0.0

    override fun update(){
        heightFromHome = heightInputAbsoluteInches - kHomeHeightInches
        Lift.demandedHeightFromHome = heightFromHome
    }

    override fun shouldFinish(): Boolean {
        return heightInputAbsoluteInches == Lift.positionFromHome
    }

    override fun stop() {
        Lift.set(HoldPosition)
    }

}