package ca.warp7.frc2019.actions

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.ActionControl
import ca.warp7.frc.control.ControllerState
import ca.warp7.frc2019.Looper
import ca.warp7.frc2019.auton.LeftRocketCloseHatch
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Lift

class Sandstorm : Action {

    private val io: BaseIO = ioInstance()
    private val autonControl = ActionControl()

    override fun start() {
        autonControl.setAction(LeftRocketCloseHatch.startToRocket)
        Looper.add(autonControl)
        io.config.apply {
            enableLimelightInput = true
            enableGyroInput = true
            enableLiftEncoderInput = true
            enableDriveEncoderInput = true
            enableDriverInput = true
            enableOperatorInput = true
        }

        val initPos = LiftConstants.kHatchStartHeightInches *
                LiftConstants.kTicksPerInch / LiftConstants.kTicksPerRadian
        io.resetLiftPosition(positionRadians = initPos)
        Lift.setpointInches = (initPos + LiftConstants.kHomeHeightInches) + 9.0
        Lift.updatePositionControl()
    }

    override fun update() {
        Drive.updateRobotStateEstimation()
    }

    override val shouldFinish: Boolean
        get() = autonControl.shouldFinish || io.driverInput.yButton == ControllerState.Pressed

    override fun stop() {
        Looper.add(MainLoop())
    }
}