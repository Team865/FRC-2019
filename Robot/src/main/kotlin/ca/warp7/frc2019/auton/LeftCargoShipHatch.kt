package ca.warp7.frc2019.auton

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.runOnce
import ca.warp7.frc.action.sequential
import ca.warp7.frc.action.wait
import ca.warp7.frc2019.actions.AlignWithLimelight
import ca.warp7.frc2019.actions.DriveForDistance
import ca.warp7.frc2019.actions.QuickTurn
import ca.warp7.frc2019.constants.LimelightMode
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.lib.withTimeout

object LeftCargoShipHatch {
    private val io: BaseIO = ioInstance()

    fun get(isLimelight: Boolean = true): Action {
        return sequential {
            // grab hatch
            +runOnce {
                io.grabbing = true
                io.pushing = false
                io.limelightMode = LimelightMode.Vision
            }
            +wait(0.5)

            // drive to second cargo bay
            +DriveForDistance(184.0 / 12 + 1.0)
            +QuickTurn(90.0).withTimeout(2.0)
            if (isLimelight) +AlignWithLimelight().withTimeout(2.0)
            +DriveForDistance(16.0 / 12)

            // outtake hatch
            +SubActions.outtakeHatch

            // drive to loading station
            +DriveForDistance(55.0 / 12, isBackwards = true)
            +QuickTurn(93.0)//.withTimeout(1.0)
            if (isLimelight) +AlignWithLimelight().withTimeout(2.0)
            +DriveForDistance(215.0 / 12)
            if (isLimelight) +AlignWithLimelight().withTimeout(2.0)
            +DriveForDistance(10.0 / 12)

            // intake hatch
            +SubActions.intakeHatch
        }
    }

    val leftSideCargoShipHatchLimelight
        get() = sequential {

            // grab hatch
            +runOnce { io.grabbing = true }
            +wait(1.0)

            // drive to second cargo bay
            +DriveForDistance(205.0 / 12 + 1.0)
            +QuickTurn(90.0).withTimeout(1.5)
            +AlignWithLimelight().withTimeout(2.0)
            +wait(0.6)
            +DriveForDistance(24.0 / 12)

            // outtake hatch
            +SubActions.outtakeHatch

            // drive to loading station
            +DriveForDistance(45.0 / 12, isBackwards = true)
            +QuickTurn(100.0).withTimeout(5.0)
            +DriveForDistance(200.0 / 12)
            +AlignWithLimelight().withTimeout(2.0)
            +DriveForDistance(30.0 / 12)

            // intake hatch
            +wait(1.5)

            // more driving to places
            +DriveForDistance(10.0 / 12, isBackwards = true)
            +wait(0.5)
            +DriveForDistance(190.0 / 12, isBackwards = true)
            +QuickTurn(-100.0).withTimeout(5.0)
            +DriveForDistance(40.0 / 12)
        }
}

