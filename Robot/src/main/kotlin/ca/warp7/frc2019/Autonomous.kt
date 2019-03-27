package ca.warp7.frc2019

import ca.warp7.actionkt.*
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Limelight
import ca.warp7.frc2019.subsystems.Outtake
import ca.warp7.frc2019.subsystems.drive.AlignWithLimelight
import ca.warp7.frc2019.subsystems.drive.DriveForDistance
import ca.warp7.frc2019.subsystems.drive.QuickTurn
import ca.warp7.frc2019.subsystems.lift.GoToSetpoint

@Suppress("unused")
object Autonomous {

    val mode get() = leftSideCargoShipHatch

    private val nothingMode get() = runOnce { }

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
            +runOnce {
                Outtake.grabbing = true
                Outtake.pushing = false
                Limelight.isDriver = false
                Limelight.isDriver = false
            }
            +wait(1.0)

            // drive to second cargo bay
            +DriveForDistance(207.0 / 12 + 1.0)
            +QuickTurn(90.0).withTimeout(2.0)
            +AlignWithLimelight().withTimeout(2.0)
            +DriveForDistance(22.0 / 12)

            // outtake hatch
            +outtakeHatch

            // drive to loading station
            +DriveForDistance(45.0 / 12, isBackwards = true)
            +QuickTurn(93.0).withTimeout(2.0)
            +AlignWithLimelight().withTimeout(2.0)
            +DriveForDistance(224.0 / 12)
            +AlignWithLimelight().withTimeout(2.0)
            +DriveForDistance(6.0 / 12)

            // intake hatch
            +intakeHatch

            // more driving to places
            +DriveForDistance(10.0 / 12, isBackwards = true)
//            +wait(0.5)
//            +DriveForDistance(190.0 / 12, isBackwards = true)
//            +QuickTurn(-100.0).withTimeout(5.0)
//            +DriveForDistance(40.0 / 12)
        }

    private val leftSideCargoShipHatchLimelight
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
            +outtakeHatch

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

    private val leftRocketFarHatchLevel2
        get() = queue {
            +DriveForDistance(200.0 / 12 + 1.0)
            +QuickTurn(-90.0)
            +async {
                val stopSignal = stopSignal
                +queue {
                    +DriveForDistance(7.0)
                    +QuickTurn(-45.0)
                    +outtakeHatch
                    +stopSignal
                }
                +GoToSetpoint(FieldConstants.kHatch2Height)
            }
            +async {
                +DriveForDistance(2.0, isBackwards = true)
                +queue {
                    wait(0.5)
                    +GoToSetpoint(LiftConstants.kHomeHeightInches)
                }
            }
        }

    private val leftRocketCloseHatchLevel2
        get() = queue {
            // off platform and turn
            +DriveForDistance(88.0 / 12 + 1.0)
            +QuickTurn(-90.0)
            +DriveForDistance(65.0 / 12)

            // turn to rocket and raise lift
            +QuickTurn(70.0)
            +async {
                val stopSignal = stopSignal
                +queue {
                    +DriveForDistance(50.0 / 12)
                    +outtakeHatch
                    +stopSignal
                }
                +GoToSetpoint(FieldConstants.kHatch2Height)
            }

            // back off and lower lift
            +async {
                +queue {
                    +DriveForDistance(3.0, isBackwards = true)
                    +QuickTurn(-160.0)
                    +DriveForDistance(160.0 / 12)
                }
                +queue {
                    wait(0.5)
                    +GoToSetpoint(LiftConstants.kHomeHeightInches)
                }
            }

            +intakeHatch
        }

    private val intakeHatch
        get() = queue {
            +runOnce { Outtake.grabbing = true }
            +wait(1.0)
        }

    private val outtakeHatch
        get() = queue {
            +runOnce { Outtake.grabbing = false }
            +runOnce { Outtake.pushing = true }
            +wait(0.5)
            +runOnce { Outtake.pushing = false }
        }

    private val quickTurn
        get() = queue {
            +QuickTurn(1.0)
            +QuickTurn(-180.0)
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
}