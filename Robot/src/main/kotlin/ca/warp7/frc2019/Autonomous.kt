package ca.warp7.frc2019

import ca.warp7.actionkt.*
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.subsystems.Outtake
import ca.warp7.frc2019.subsystems.drive.DriveForDistance
import ca.warp7.frc2019.subsystems.drive.QuickTurn
import ca.warp7.frc2019.subsystems.lift.GoToSetpoint

@Suppress("unused")
object Autonomous {

    val mode get() = leftSideCargoShipHatch

    private val nothingMode = { runOnce { } }

    private val quickTurn
        get() = queue {
            +QuickTurn(1.0)
            +QuickTurn(-180.0)
        }

    private val driveBackgroundSubtractor
        get() = queue {
            +QuickTurn(180.0)
            +wait(0.5)
            +DriveForDistance(8.0)
        }

    private val driveBackingStoreException
        get() = queue {
            +DriveForDistance(8.0)
            +wait(0.5)
            +QuickTurn(180.0)
            +wait(0.5)
            +DriveForDistance(8.0)
            +wait(0.5)
            +QuickTurn(180.0)
        }

    private val straightHatch
        get() = queue {
            +runOnce { Outtake.grabbing = true }
            +DriveForDistance(138.0 / 12)
            +wait(0.5)
            +runOnce { Outtake.grabbing = false }
            +wait(0.2)
            +runOnce { Outtake.pushing = true }
            +wait(0.5)
            +runOnce { Outtake.pushing = false }
        }

    private val leftSideCargoShipHatch
        get() = queue {

            // grab hatch
            +runOnce { Outtake.grabbing = true }

            // drive to second cargo bay
            +DriveForDistance(197.0 / 12 + 1.0)
            +QuickTurn(90.0).withTimeout(5.0)
            +DriveForDistance(24.0 / 12)

            // outtake hatch
            +outtakeHatch

            // drive to loading station
            +DriveForDistance(45.0 / 12, isBackwards = true)
            +QuickTurn(100.0).withTimeout(5.0)
            +DriveForDistance(230.0 / 12)

            // intake hatch
            +wait(1.5)

            // more driving to places
            +DriveForDistance(10.0 / 12, isBackwards = true)
            +wait(0.5)
            +DriveForDistance(190.0 / 12, isBackwards = true)
            +QuickTurn(-100.0).withTimeout(5.0)
            +DriveForDistance(40.0 / 12)
        }

    private val leftRocketFarHatch
        get() = queue {
            +DriveForDistance(200.0 / 12 + 1.0)
            +QuickTurn(-90.0)
            +async {
                +queue {
                    +DriveForDistance(7.0)
                    +QuickTurn(-45.0)
                }
                +GoToSetpoint().apply { setpoint = FieldConstants.secondHatchPortCenterHeightInches }
            }
            +outtakeHatch
        }

    private val outtakeHatch
        get() = queue {
            +runOnce { Outtake.grabbing = false }
            +wait(0.1)
            +runOnce { Outtake.pushing = true }
            +wait(0.3)
            +runOnce { Outtake.pushing = false }
        }
}