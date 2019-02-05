package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Lift

object HoldPosition : Action{
    var positionToHold = 0.0
    override fun start() {
        positionToHold = Lift.measuredPosition
    }

    override fun update() {
        Lift.demand = positionToHold
    }

    override val shouldFinish: Boolean
        get() {
            return false
        }
}