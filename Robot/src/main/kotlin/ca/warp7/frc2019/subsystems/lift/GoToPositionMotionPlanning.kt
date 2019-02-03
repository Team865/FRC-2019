package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift

object GoToPositionMotionPlanning : Action {
    var heightInputAbsoluteInches = 0.0
    var heightFromHome = 0.0
    var tragectory = liftTragectory
    var timeStarted = 0.0

    fun generateTragectory(targetHeightAbsolute: Double){
        timeStarted = 0.0 //TODO get time and pass dt
        heightInputAbsoluteInches = targetHeightAbsolute - LiftConstants.kHomeHeightInches
        tragectory.generateTragectory(heightFromHome)
    }
    override fun update(){
        Lift.demandedVelocity = tragectory.desiredVelocoity(0.0)
    }
}