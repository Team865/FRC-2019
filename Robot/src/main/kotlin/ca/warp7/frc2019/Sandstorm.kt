package ca.warp7.frc2019

import ca.warp7.actionkt.Action
import ca.warp7.frc.ControllerState
import ca.warp7.frc.Controls
import ca.warp7.frc.RobotControl
import ca.warp7.frc.set
import ca.warp7.frc2019.auton.StraightHatch
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.Outtake
import ca.warp7.frc2019.subsystems.drive.DriveState
import ca.warp7.frc2019.subsystems.lift.LiftState

object Sandstorm : Action {

    fun getAutoMode(): Action {
        return StraightHatch.straightHatch
    }

    private lateinit var autoAction: Action

    override fun start() {
        println("Robot State: Sandstorm")
        Drive.set(DriveState.kNeutralOutput)
        Lift.set(LiftState.kIdle)
        Outtake.set {
            grabbing = true
            pushing = false
        }
        autoAction = getAutoMode()
        autoAction.start()
    }

    override val shouldFinish: Boolean
        get() = autoAction.shouldFinish || Controls.robotDriver.yButton == ControllerState.Pressed // switch to teleop

    override fun update() {
        autoAction.update()
    }

    override fun stop() {
        autoAction.stop()
        RobotControl.set(MainLoop)
    }
}