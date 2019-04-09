package ca.warp7.frc2019.auton

import ca.warp7.actionkt.queue
import ca.warp7.actionkt.runOnce
import ca.warp7.actionkt.wait
import ca.warp7.frc.PID
import ca.warp7.frc.path.waypoint
import ca.warp7.frc2019.subsystems.Limelight
import ca.warp7.frc2019.subsystems.Outtake
import ca.warp7.frc2019.subsystems.drive.*
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner.robotState
import ca.warp7.frc2019.subsystems.drive.unused.PIDToPoint
import ca.warp7.frc2019.subsystems.drive.unused.PIDTrajectory

object DriveOnly {
    val driveForDistance = queue{
        +runOnce{Limelight.isDriver=false}
        +wait(1000.0)
    }

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
            +runOnce{
                Outtake.grabbing=true
                Outtake.pushing=false
            }
            +PIDTrajectory(
                    arrayOf(
                            waypoint(0, 0, 0),
                            waypoint(2, 0, 0),
                            waypoint(16.7, 0, -90)
                    ),
                    maxAccelerationRatio = 1.0,
                    maxVelocityRatio = 1.0,
                    angularDrag = 1.0,
                    enableFeedback = false,
                    enableFeedforward = true,
                    kA = 6.0,
                    lateralKp = 0.0,
                    lookaheadDist = 10000.0,
                    straightPID = PID(
                            kP = 0.15, kI = 0.0015, kD = 0.3, kF = 0.1,
                            //kP = 0.0, kI = 0.0, kD = 0.0,
                            //kP = 0.00, kI = 0.0, kD = 0.0,
                            errorEpsilon = 0.25, dErrorEpsilon = 0.2, minTimeInEpsilon = 0.3
                    ),
                    turnPID = PID(
                            kP = 2.0, kI = 0.08, kD = 5.0, kF = 0.0,
                            //kP = 0.0, kI = 0.0, kD = 0.0,
                            //kP = 0.02, kI = 0.0, kD = 0.0,
                            errorEpsilon = 2.0, dErrorEpsilon = 1.0, minTimeInEpsilon = 0.3
                    )
            )
            +runOnce { Limelight.isDriver = false }
            +AlignWithLimelight()
            +wait(0.3)
            +DriveForDistance(1.9)
            +runOnce{
                Outtake.grabbing=false
                Outtake.pushing=true
            }
            +wait(0.3)
            +DriveForDistance(3.0, isBackwards = true)

            +runOnce{
                Outtake.pushing=false
            }
            //+QuickTurn(-135.0)
            //+PIDToPoint(waypoint(13, 0, -90))
        }

}