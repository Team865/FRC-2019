package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Lift

object GoToPositionMotionPlanning : Action {
    var positionInput = 0.0

    override fun update(){
        Lift.demandedHeightFromHome = positionInput
    }
}