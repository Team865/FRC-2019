package ca.warp7.frc2019.auton

import ca.warp7.actionkt.async
import ca.warp7.actionkt.queue
import ca.warp7.actionkt.wait
import ca.warp7.frc2019.actions.GoToSetpoint
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.drive.DriveForDistance
import ca.warp7.frc2019.subsystems.drive.QuickTurn

object LeftRocketCloseHatch {

    val level2
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
                    +SubActions.outtakeHatch
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

            +SubActions.intakeHatch
        }
}