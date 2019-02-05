package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.lift.planner.LiftMotionPlanner
import edu.wpi.first.wpilibj.Timer

object GoToPositionMotionPlanning : Action {
    var heightInputAbsoluteInches = 0.0
    var targetHeightFromHome = 0.0
    var tragectory = LiftTrajectory
    var timeStarted = 0.0

    override fun start() {
        Lift.outputType = Lift.OutputType.Velocity
    }

    fun generateTragectory(targetHeightAbsolute: Double) {
        timeStarted = Timer.getFPGATimestamp()
        heightInputAbsoluteInches = targetHeightAbsolute - LiftConstants.kHomeHeightInches
        tragectory.generateTrajectory(targetHeightFromHome)
    }

    override fun update() {
        Lift.demand = tragectory.desiredVelocoity(Timer.getFPGATimestamp() - timeStarted)
    }

    override val shouldFinish: Boolean
        get() {
            return LiftMotionPlanner.currentHeight == targetHeightFromHome && LiftMotionPlanner.currentVelocity == Lift.demand
        }
}