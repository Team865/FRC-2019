package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Lift

object HoldPosition : Action{
    var positionToHold = 0.0
    override fun start() {
        positionToHold = Lift.positionFromHome
    }

    override fun update() {
        Lift.demandedHeightFromHome = positionToHold
    }

    override fun shouldFinish(): Boolean {
        return false
    }
}