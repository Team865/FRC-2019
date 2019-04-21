package ca.warp7.frc2019.subsystems.lift.deprecated

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.v2.subsystems.Lift
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer

@Suppress("unused")
object GoToPositionMotionPlanning : Action {
    private val io: RobotIO = RobotIO
    var heightInputAbsoluteInches = 0.0
    var targetHeightFromHome = 0.0
    var tragectory = LiftTrajectory
    var timeStarted = 0.0

    override fun start() {
        io.liftControlMode = ControlMode.Velocity
    }

    fun generateTragectory(targetHeightAbsolute: Double) {
        timeStarted = Timer.getFPGATimestamp()
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