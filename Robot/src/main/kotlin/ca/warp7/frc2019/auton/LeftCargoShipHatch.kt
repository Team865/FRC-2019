package ca.warp7.frc2019.auton

import ca.warp7.actionkt.*
import ca.warp7.frc2019.subsystems.Limelight
import ca.warp7.frc2019.subsystems.Outtake
import ca.warp7.frc2019.subsystems.drive.AlignWithLimelight
import ca.warp7.frc2019.subsystems.drive.DriveForDistance
import ca.warp7.frc2019.subsystems.drive.QuickTurn

object LeftCargoShipHatch {
    fun get(isLimelight: Boolean = true): Action {
        return queue {
            // grab hatch
            +runOnce {
                Outtake.grabbing = true
                Outtake.pushing = false
                Limelight.isDriver = false
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
            +QuickTurn(93.0).withTimeout(1.0)
            if (isLimelight) +AlignWithLimelight().withTimeout(2.0)
            +DriveForDistance(215.0 / 12)
            if (isLimelight) +AlignWithLimelight().withTimeout(2.0)
            +DriveForDistance(10.0 / 12)

            // intake hatch
            +SubActions.intakeHatch
        }
    }

    val leftSideCargoShipHatchLimelight
        get() = queue {

            // grab hatch
            +runOnce { Outtake.grabbing = true }
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

