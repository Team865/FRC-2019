package ca.warp7.frc2019.auton

import ca.warp7.actionkt.queue
import ca.warp7.actionkt.runOnce
import ca.warp7.actionkt.wait
import ca.warp7.frc.PID
import ca.warp7.frc.path.waypoint
import ca.warp7.frc2019.subsystems.Limelight
import ca.warp7.frc2019.subsystems.drive.AlignWithLimelight
import ca.warp7.frc2019.subsystems.drive.DriveForDistance
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner.robotState
import ca.warp7.frc2019.subsystems.drive.DriveStraightPID
import ca.warp7.frc2019.subsystems.drive.QuickTurn
import ca.warp7.frc2019.subsystems.drive.unused.PIDToPoint
import ca.warp7.frc2019.subsystems.drive.unused.PIDTrajectory

object DriveOnly {
    val driveForDistance = queue {
        +DriveStraightPID(0.5)
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
            //+SetRobotState(8.4717, 4.2358, 0.0000)
            +PIDTrajectory(
                    arrayOf(
                            waypoint(0, 0, 0),
                            waypoint(2, 0, 0),
                            waypoint(15.6, 0, -90)
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
            +runOnce{ Limelight.isDriver=false}
            +AlignWithLimelight()
            +DriveForDistance(1.4)
            +DriveForDistance(3.0, isBackwards=true)

            +PIDTrajectory(
                    arrayOf(
                            robotState,
                            waypoint(9, 0, 180)
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
            //+PIDToPoint(waypoint(13, 0, -90))
        }

}