package ca.warp7.frc2019.test

import ca.warp7.frc2019.test.commons.DriveWithCommons
import ca.warp7.frc2019.test.drive.DriveLinearPID
import edu.wpi.first.wpilibj.TimedRobot

object TestRobot {
    @JvmStatic
    fun main(args: Array<String>) {
        TimedRobot.startRobot { DriveLinearPID() }
    }
}