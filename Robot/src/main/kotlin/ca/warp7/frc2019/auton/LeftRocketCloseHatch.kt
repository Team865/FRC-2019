package ca.warp7.frc2019.auton

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.async
import ca.warp7.actionkt.queue
import ca.warp7.actionkt.wait
import ca.warp7.frc.feet
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.degrees
import ca.warp7.frc2019.actions.DriveForDistance
import ca.warp7.frc2019.actions.DriveTrajectory
import ca.warp7.frc2019.actions.LiftSetpoint
import ca.warp7.frc2019.actions.QuickTurn
import ca.warp7.frc2019.constants.DriveFollower.Ramsete
import ca.warp7.frc2019.constants.DriveFollower.VoltageOnly
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.LiftConstants

object LeftRocketCloseHatch {

    val startPose = Pose2D(6.0.feet, 4.0.feet, 0.degrees)
    val rocketPose = Pose2D(16.8.feet, 11.2.feet, 32.degrees)
    val turnPose = Pose2D(14.0.feet, 7.0.feet, 90.degrees)
    val loadingStationPose = Pose2D(0.0.feet, 8.feet, 180.degrees)

    val startToRocket: Action
        get() = DriveTrajectory(arrayOf(startPose, rocketPose), follower = VoltageOnly)

    val rocketToLoadingStation: Action
        get() = queue {
            +DriveTrajectory(arrayOf(rocketPose, turnPose), backwards = true, follower = Ramsete)
            +DriveTrajectory(arrayOf(turnPose, loadingStationPose), follower = Ramsete)
        }

    val level1
        get() = queue {
            +startToRocket
            +SubActions.outtakeHatch
            +rocketToLoadingStation
            +SubActions.intakeHatch
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