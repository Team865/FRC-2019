package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift
import edu.wpi.first.wpilibj.Timer

object GoToPositionMotionPlanning : Action {
    var heightInputAbsoluteInches = 0.0
    var heightFromHome = 0.0
    var tragectory = LiftTragectory
    var timeStarted = 0.0

    fun generateTragectory(targetHeightAbsolute: Double){
        timeStarted = Lift.time
        heightInputAbsoluteInches = targetHeightAbsolute - LiftConstants.kHomeHeightInches
        tragectory.generateTragectory(heightFromHome)
    }
    override fun update(){
        Lift.demandedVelocity = tragectory.desiredVelocoity(Lift.time)
    }

    override fun shouldFinish(): Boolean {
        //TODO
        return true

    }
}