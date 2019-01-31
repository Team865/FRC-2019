package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Lift


object GoToPosition: Action {
    var positionInput = 0.0
    override fun update(){
        Lift.demandedPosition = positionInput
    }
}