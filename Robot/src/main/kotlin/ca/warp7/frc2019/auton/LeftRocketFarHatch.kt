package ca.warp7.frc2019.auton

import ca.warp7.frc.action.parallel
import ca.warp7.frc.action.sequential
import ca.warp7.frc.action.wait
import ca.warp7.frc2019.actions.DriveForDistance
import ca.warp7.frc2019.actions.LiftSetpoint
import ca.warp7.frc2019.actions.QuickTurn
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.LiftConstants

object LeftRocketFarHatch {
    val level2
        get() = sequential {
            +DriveForDistance(200.0 / 12 + 1.0)
            +QuickTurn(-90.0)
            +parallel {
                //                val stopSignal = stopSignal
                +sequential {
                    +DriveForDistance(7.0)
                    +QuickTurn(-45.0)
                    +SubActions.outtakeHatch
                    //                    +stopSignal
                }
                +LiftSetpoint(FieldConstants.kHatch2Height)
            }
            +parallel {
                +DriveForDistance(2.0, isBackwards = true)
                +sequential {
                    wait(0.5)
                    +LiftSetpoint(LiftConstants.kHomeHeightInches)
                }
            }
        }
}