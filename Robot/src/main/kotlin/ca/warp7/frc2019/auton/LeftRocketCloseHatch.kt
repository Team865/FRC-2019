package ca.warp7.frc2019.auton

import ca.warp7.frc.action.Action
import ca.warp7.frc.action.parallel
import ca.warp7.frc.action.sequential
import ca.warp7.frc.action.wait
import ca.warp7.frc.degrees
import ca.warp7.frc.feet
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc2019.actions.DriveTrajectory2
import ca.warp7.frc2019.actions.LiftSetpoint
import ca.warp7.frc2019.actions.QuickTurn
import ca.warp7.frc2019.actions.driveStraight
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.followers.RamseteFollower
import ca.warp7.frc2019.followers.SpeedDemandFollower

object LeftRocketCloseHatch {

    val startPose = Pose2D(6.0.feet, 4.0.feet, 0.degrees)
    val rocketPose = Pose2D(16.8.feet, 11.2.feet, 32.degrees)
    val turnPose = Pose2D(14.0.feet, 7.0.feet, 90.degrees)
    val loadingStationPose = Pose2D(0.0.feet, 8.feet, 180.degrees)

    fun startToRocket(): Action = DriveTrajectory2(SpeedDemandFollower()) {
        startAt(Pose2D.identity)
        moveTo(Pose2D(16.feet, 0.0, 90.degrees.inverse))
    }

    fun rocketToLoadingStation(): Action = sequential {
        +DriveTrajectory2(RamseteFollower()) {
            startAt(startPose)
            moveTo(rocketPose)
            moveTo(turnPose)
            setInverted(true)
        }
        +DriveTrajectory2(RamseteFollower()) {
            startAt(startPose)
            moveTo(turnPose)
            moveTo(loadingStationPose)
        }
    }

    fun level1() = sequential {
        +startToRocket()
        +SubActions.outtakeHatch()
        +rocketToLoadingStation()
        +SubActions.intakeHatch()
    }

    fun level2() = sequential {
        // off platform and turn
        +driveStraight(88.0 / 12 + 1.0)
        +QuickTurn(-90.0)
        +driveStraight(65.0 / 12)

        // turn to rocket and raise lift
        +QuickTurn(70.0)
        +parallel {
            +sequential {
                +driveStraight(50.0 / 12)
                +SubActions.outtakeHatch()
                //+stopSignal
            }
            +LiftSetpoint(FieldConstants.kHatch2Height)
        }

        // back off and lower lift
        +parallel {
            +sequential {
                +driveStraight(3.0, isBackwards = true)
                +QuickTurn(-160.0)
                +driveStraight(160.0 / 12)
            }
            +sequential {
                wait(0.5)
                +LiftSetpoint(LiftConstants.kHomeHeightInches)
            }
        }

        +SubActions.intakeHatch()
    }
}