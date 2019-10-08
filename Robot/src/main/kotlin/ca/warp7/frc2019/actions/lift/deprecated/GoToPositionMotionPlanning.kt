package ca.warp7.frc2019.actions.lift.deprecated

import ca.warp7.frc.action.Action
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Lift
import com.ctre.phoenix.motorcontrol.ControlMode

@Suppress("unused")
object GoToPositionMotionPlanning : Action {
    private val io: BaseIO = ioInstance()
    var heightInputAbsoluteInches = 0.0
    var targetHeightFromHome = 0.0
    var tragectory = LiftTrajectory
    var timeStarted = 0.0

    override fun firstCycle() {
        io.liftControlMode = ControlMode.Velocity
    }

    fun generateTragectory(targetHeightAbsolute: Double) {
        timeStarted = io.time
        heightInputAbsoluteInches = targetHeightAbsolute - LiftConstants.kHomeHeightInches
        LiftTrajectory.generateTrajectory(targetHeightFromHome)
    }

    override fun update() {
        io.liftDemand = LiftTrajectory.desiredVelocoity(io.time - timeStarted)
    }

    override val shouldFinish: Boolean
        get() {
            return Lift.height == targetHeightFromHome && io.liftVelocity == io.liftDemand.toInt()
        }
}