package ca.warp7.frc2019.test

import ca.warp7.frc2019.test.drive.DriveCurvature
import ca.warp7.frc2019.test.drive.DriveLinearPIDCopy
import ca.warp7.frc2019.test.drive.TrapezoidalVelocityDriveTest
import ca.warp7.frc2019.test.drive.simpleTrapezoid
import edu.wpi.first.wpilibj.TimedRobot

object TestRobot {
    @JvmStatic
    fun main(args: Array<String>) {
        TimedRobot.startRobot { simpleTrapezoid() }
    }
}