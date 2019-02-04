package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants

// TODO This is an unfinished and unused option for varying lift controls
object GoToPositionBase : Action {

    val kLinear = GoToPosition
    val kPlanned = GoToPositionMotionPlanning
    val kOptimised = GoToPositionMotionPlanningSimple

    var heightInputAbsoluteInches = 0.0
    var targetHeightFromHome = 0.0
    var tragectory = LiftTragectory
    var timeStarted = 0.0

    val type = LiftConstants.kType

    override fun start() {
        super.start()
    }
    override fun update(){
        when (type){
            liftMotionType.LinearPID -> GoToPosition.update()
            liftMotionType.PlannedMotion -> TODO()
            liftMotionType.OptimisedMotion -> TODO()
        }
    }

    override val shouldFinish: Boolean
        get() = super.shouldFinish
}