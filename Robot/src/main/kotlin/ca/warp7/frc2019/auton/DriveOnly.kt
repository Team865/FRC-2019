package ca.warp7.frc2019.auton

import ca.warp7.actionkt.queue
import ca.warp7.actionkt.runOnce
import ca.warp7.actionkt.wait
import ca.warp7.frc.feet
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.degrees
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.actions.AlignWithLimelight
import ca.warp7.frc2019.actions.DriveForDistance
import ca.warp7.frc2019.actions.DriveTrajectory
import ca.warp7.frc2019.actions.QuickTurn
import ca.warp7.frc2019.constants.LimelightMode

object DriveOnly {
    private val io: RobotIO = RobotIO
    val driveForDistance = queue {}

    val quickTurn
        get() = queue {
            +QuickTurn(1.0)
            +QuickTurn(-180.0)
        }

    val driveBackingStoreException
        get() = queue {
            +DriveForDistance(8.0)
            +wait(0.5)
            +QuickTurn(180.0)
            +wait(0.5)
            +DriveForDistance(8.0)
            +wait(0.5)
            +QuickTurn(180.0)
        }

    val leftCloseRocket
        get() = queue {
            //            //+SetRobotState(8.4717, 4.2358, 0.0000)
            +runOnce {
                io.grabbing = true
                io.pushing = false
            }
            +DriveTrajectory(
                    arrayOf(
                            Pose2D(0.feet, 0.feet, 0.degrees),
                            Pose2D(2.feet, 0.feet, 0.degrees),
                            Pose2D(2.feet, 0.feet, 0.degrees),
                            Pose2D(16.7.feet, 0.feet, 90.degrees)
                    )
            )
            +runOnce { io.limelightMode = LimelightMode.Vision }
            +AlignWithLimelight()
            +wait(0.3)
            +DriveForDistance(1.9)
            +runOnce {
                io.grabbing = false
                io.pushing = true
            }
            +wait(0.3)
            +DriveForDistance(3.0, isBackwards = true)

            +runOnce {
                io.pushing = false
            }
            //+QuickTurn(-135.0)
            //+PIDToPoint(waypoint(13, 0, -90))
        }

}