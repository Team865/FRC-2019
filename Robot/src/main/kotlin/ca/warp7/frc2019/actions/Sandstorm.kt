package ca.warp7.frc2019.actions

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.ActionControl
import ca.warp7.frc.inputs.ButtonState
import ca.warp7.frc2019.Looper
import ca.warp7.frc2019.auton.LeftRocketCloseHatch
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive

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

//        val initPos = LiftConstants.kHatchStartHeightInches *
//                LiftConstants.kTicksPerInch / LiftConstants.kTicksPerRadian
//        io.resetLiftPosition(positionRadians = initPos)
        //Lift.setpointInches = (initPos + LiftConstants.kHomeHeightInches) + 9.0
        //Lift.updatePositionControl()
    }

    override fun update() {
        Drive.updateRobotStateEstimation()
    }

    override val shouldFinish: Boolean
        get() = autonControl.shouldFinish() || io.driverInput.yButton == ButtonState.Pressed

    override fun stop() {
        Looper.add(MainLoop())
    }
}