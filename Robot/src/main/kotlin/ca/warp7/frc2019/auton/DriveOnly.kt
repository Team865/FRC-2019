package ca.warp7.frc2019.auton


import ca.warp7.frc.action.runOnce
import ca.warp7.frc.action.sequential
import ca.warp7.frc.action.wait
import ca.warp7.frc.feet
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.degrees
import ca.warp7.frc2019.actions.AlignWithLimelight
import ca.warp7.frc2019.actions.DriveTrajectory2
import ca.warp7.frc2019.actions.QuickTurn
import ca.warp7.frc2019.actions.driveStraight
import ca.warp7.frc2019.constants.LimelightMode
import ca.warp7.frc2019.followers.VoltageOnlyFollower
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance

object DriveOnly {
    private val io: BaseIO = ioInstance()

    val driveForDistance = sequential {}

    fun quickTurn() = sequential {
        +QuickTurn(1.0)
        +QuickTurn(-180.0)
    }

    fun driveBackingStoreException() = sequential {
        +driveStraight(8.0)
        +wait(0.5)
        +QuickTurn(180.0)
        +wait(0.5)
        +driveStraight(8.0)
        +wait(0.5)
        +QuickTurn(180.0)
    }

    fun leftCloseRocket() = sequential {
        +runOnce {
            io.grabbing = true
            io.pushing = false
        }
        +DriveTrajectory2 {
            startAt(Pose2D.identity)
            moveTo(Pose2D(2.feet, 0.feet, 0.degrees))
            moveTo(Pose2D(16.7.feet, 0.feet, 90.degrees))
            setFollower(VoltageOnlyFollower())
        }
        +runOnce { io.limelightMode = LimelightMode.Vision }
        +AlignWithLimelight()
        +wait(0.3)
        +driveStraight(1.9.feet)
        +runOnce {
            io.grabbing = false
            io.pushing = true
        }
        +wait(0.3)
        +driveStraight((-3.0).feet)
        +runOnce {
            io.pushing = false
        }
        //+QuickTurn(-135.0)
        //+PIDToPoint(waypoint(13, 0, -90))
    }

}