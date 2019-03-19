package ca.warp7.frc2019.subsystems.lift.DEPRECATED

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.lift.LiftMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer

@Suppress("unused")
object GoToPositionMotionPlanning : Action {
    var heightInputAbsoluteInches = 0.0
    var targetHeightFromHome = 0.0
    var tragectory = LiftTrajectory
    var timeStarted = 0.0

    override fun start() {
        Lift.controlMode = ControlMode.Velocity
    }

    fun generateTragectory(targetHeightAbsolute: Double) {
        timeStarted = Timer.getFPGATimestamp()
        heightInputAbsoluteInches = targetHeightAbsolute - LiftConstants.kHomeHeightInches
        LiftTrajectory.generateTrajectory(targetHeightFromHome)
    }

    override fun update() {
        Lift.demand = LiftTrajectory.desiredVelocoity(Timer.getFPGATimestamp() - timeStarted)
    }

    override val shouldFinish: Boolean
        get() {
            return LiftMotionPlanner.height == targetHeightFromHome && LiftMotionPlanner.velocity == Lift.demand
        }
}