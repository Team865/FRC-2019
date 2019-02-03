package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants.kHomeHeightInches
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.LiftState

object GoToPositionMotionPlanning: Action {
    var heightInputAbsoluteInches = 0.0
    var heightFromHome = 0.0

    override fun update() {
        heightFromHome = heightInputAbsoluteInches - kHomeHeightInches

    }
}