package ca.warp7.frc2019.auton

import ca.warp7.frc.action.*
import ca.warp7.frc2019.actions.AlignWithLimelight
import ca.warp7.frc2019.actions.QuickTurn
import ca.warp7.frc2019.actions.driveStraight
import ca.warp7.frc2019.constants.LimelightMode
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance

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
            +driveStraight(184.0 / 12 + 1.0)
            +QuickTurn(90.0).withTimeout(2.0)
            if (isLimelight) +AlignWithLimelight().withTimeout(2.0)
            +driveStraight(16.0 / 12)

            // outtake hatch
            +SubActions.outtakeHatch()

            // drive to loading station
            +driveStraight(55.0 / 12, isBackwards = true)
            +QuickTurn(93.0)//.withTimeout(1.0)
            if (isLimelight) +AlignWithLimelight().withTimeout(2.0)
            +driveStraight(215.0 / 12)
            if (isLimelight) +AlignWithLimelight().withTimeout(2.0)
            +driveStraight(10.0 / 12)

            // intake hatch
            +SubActions.intakeHatch()
        }
    }

    fun leftSideCargoShipHatchLimelight() = sequential {

        // grab hatch
        +runOnce { io.grabbing = true }
        +wait(1.0)

        // drive to second cargo bay
        +driveStraight(205.0 / 12 + 1.0)
        +QuickTurn(90.0).withTimeout(1.5)
        +AlignWithLimelight().withTimeout(2.0)
        +wait(0.6)
        +driveStraight(24.0 / 12)

        // outtake hatch
        +SubActions.outtakeHatch()

        // drive to loading station
        +driveStraight(45.0 / 12, isBackwards = true)
        +QuickTurn(100.0).withTimeout(5.0)
        +driveStraight(200.0 / 12)
        +AlignWithLimelight().withTimeout(2.0)
        +driveStraight(30.0 / 12)

        // intake hatch
        +wait(1.5)

        // more driving to places
        +driveStraight(10.0 / 12, isBackwards = true)
        +wait(0.5)
        +driveStraight(190.0 / 12, isBackwards = true)
        +QuickTurn(-100.0).withTimeout(5.0)
        +driveStraight(40.0 / 12)
    }
}

