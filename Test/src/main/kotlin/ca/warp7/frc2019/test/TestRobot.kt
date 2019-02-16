package ca.warp7.frc2019.test

import ca.warp7.frc2019.test.drive.DriveCurvature
import ca.warp7.frc2019.test.lift.LiftFeedforward
import ca.warp7.frc2019.test.low_goal_bot.Lowbot
import ca.warp7.frc2019.test.commons.DriveWithCommons
import edu.wpi.first.wpilibj.TimedRobot

object TestRobot {
    @JvmStatic
    fun main(args: Array<String>) {
        TimedRobot.startRobot { DriveWithCommons() }
    }
}