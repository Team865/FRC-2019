package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Lift

object OpenLoopLift : Action {
    var percentOut = 0.0

    override fun update(){
        Lift.percentOutput = percentOut
    }
}