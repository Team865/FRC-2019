package ca.warp7.frc2019

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.Looper
import ca.warp7.frc.input.ButtonState
import ca.warp7.frc2019.auton.StraightHatch
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive

class Sandstorm : Action {

    private val io: BaseIO = ioInstance()

    private val auto = StraightHatch.straightHatch

    override fun firstCycle() {
        Looper.add(auto)
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

    override fun shouldFinish(): Boolean {
        return auto.shouldFinish() || io.driverInput.yButton == ButtonState.Pressed
    }

    override fun lastCycle() {
        Looper.add(MainLoop())
    }
}