package ca.warp7.frc2019.test

import ca.warp7.frc2019.test.drive.DriveLinearPIDCopy
import edu.wpi.first.wpilibj.TimedRobot

object TestRobot {
    @JvmStatic
    fun main(args: Array<String>) {
        TimedRobot.startRobot { DriveLinearPIDCopy() }
    }
}