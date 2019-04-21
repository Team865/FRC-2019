package ca.warp7.frc2019.actions

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.ActionControl
import ca.warp7.frc.control.ControllerState
import ca.warp7.frc2019.Looper
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.auton.DriveOnly
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift

class Sandstorm : Action {

    private val io: RobotIO = RobotIO
    private val autonControl = ActionControl()

    override fun start() {
        autonControl.setAction(DriveOnly.leftCloseRocket)
        Looper.add(RobotStateEstimator())
        Looper.add(autonControl)
        io.readingLimelight = true
        io.readingGyro = true
        io.readingLiftEncoder = true
        io.readingDriveEncoders = true
        io.readingDriverInput = true
        io.readingOperatorInput = true

        val initPos = LiftConstants.kHatchStartHeightInches *
                LiftConstants.kTicksPerInch / LiftConstants.kTicksPerRadian
        io.resetLiftPosition(positionRadians = initPos)
        Lift.setpointInches = (initPos + LiftConstants.kHomeHeightInches) + 9.0
        Lift.updatePositionControl() // todo repeat calls?
    }

    override val shouldFinish: Boolean
        get() = autonControl.shouldFinish || io.driver.yButton == ControllerState.Pressed

    override fun stop() {
        Looper.add(MainLoop())
    }
}