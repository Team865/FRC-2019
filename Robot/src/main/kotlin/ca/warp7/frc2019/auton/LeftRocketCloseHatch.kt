package ca.warp7.frc2019.auton

import ca.warp7.actionkt.async
import ca.warp7.actionkt.queue
import ca.warp7.actionkt.wait
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.degrees
import ca.warp7.frc2019.actions.DriveForDistance
import ca.warp7.frc2019.actions.DriveTrajectory
import ca.warp7.frc2019.actions.LiftSetpoint
import ca.warp7.frc2019.actions.QuickTurn
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.FollowerType
import ca.warp7.frc2019.constants.LiftConstants

object LeftRocketCloseHatch {


    val level1
        get() = queue {
            +DriveTrajectory(arrayOf(
                    Pose2D(6.0, 4.0, 0.degrees),
                    Pose2D(16.8, 11.2, 32.degrees)
            ), followerType = FollowerType.Ramsete)
            +SubActions.outtakeHatch
        }

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
                +LiftSetpoint(FieldConstants.kHatch2Height)
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
                    +LiftSetpoint(LiftConstants.kHomeHeightInches)
                }
            }

            +SubActions.intakeHatch
        }
}