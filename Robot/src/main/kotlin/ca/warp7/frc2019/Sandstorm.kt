package ca.warp7.frc2019

import ca.warp7.actionkt.Action
import ca.warp7.frc.control.ControllerState
import ca.warp7.frc.control.Controls
import ca.warp7.frc.control.RobotControl
import ca.warp7.frc.control.set
import ca.warp7.frc2019.auton.DriveOnly
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.Outtake
import ca.warp7.frc2019.subsystems.drive.DriveState
import ca.warp7.frc2019.subsystems.lift.LiftState

object Sandstorm : Action {

    fun getAutoMode(): Action {
        return DriveOnly.driveForDistance
    }

    private lateinit var autoAction: Action

    override fun start() {
        println("Robot State: Sandstorm")
        Drive.set(DriveState.kNeutralOutput)

        val initPos = LiftConstants.kHatchStartHeightInches
        Lift.master.selectedSensorPosition = -(initPos * LiftConstants.kTicksPerInch).toInt()
        Lift.set(LiftState.kGoToSetpoint) { setpoint = (initPos + LiftConstants.kHomeHeightInches)+9.0 }

        Outtake.set {
            speed = 0.0
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