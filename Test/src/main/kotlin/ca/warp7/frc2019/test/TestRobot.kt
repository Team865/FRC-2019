package ca.warp7.frc2019.test

import ca.warp7.frc2019.test.lift.feedforward.LiftFeedforward2
import edu.wpi.first.wpilibj.TimedRobot

object TestRobot {
    @JvmStatic
    fun main(args: Array<String>) {
        TimedRobot.startRobot { LiftFeedforward2() }
    }
}