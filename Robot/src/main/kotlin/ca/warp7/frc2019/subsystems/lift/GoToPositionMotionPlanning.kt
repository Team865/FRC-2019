package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants

object GoToPositionMotionPlanning : Action {
    var heightInputAbsoluteInches = 0.0
    var heightFromHome = 0.0
    var tragectory = LiftTragectory

    override fun start() {
        tragectory =
    }
    override fun update(){
        heightFromHome = heightInputAbsoluteInches - LiftConstants.kHomeHeightInches

    }
}