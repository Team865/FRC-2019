package ca.warp7.frc2019.auton

import ca.warp7.actionkt.queue
import ca.warp7.actionkt.wait
import ca.warp7.frc.path.waypoint
import ca.warp7.frc2019.subsystems.drive.DriveForDistance
import ca.warp7.frc2019.subsystems.drive.QuickTurn
import ca.warp7.frc2019.subsystems.drive.SetRobotState
import ca.warp7.frc2019.subsystems.drive.unused.PIDTrajectory

object DriveOnly {
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

    val leftCloseRocket
        get() = queue {
            +SetRobotState(6.0000, -4.0000, 0.0000)
            +PIDTrajectory(
                    arrayOf(
                            waypoint(6.0000, -4.0000, 0.0000),
                            waypoint(10.0000, -4.0000, -0.0000),
                            waypoint(14.1000, -8.3000, -69.0000),
                            waypoint(17.5000, -11.5000, -30.0000)
                    ),
                    enableFeedback = false
            )
        }

}