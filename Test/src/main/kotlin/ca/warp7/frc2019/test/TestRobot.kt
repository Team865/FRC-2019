package ca.warp7.frc2019.test

import ca.warp7.frc2019.test.Passthrough.Passthrough
import ca.warp7.frc2019.test.conveyor_simple.ConveyorSimple
import ca.warp7.frc2019.test.intake_simple.IntakeSimple
import ca.warp7.frc2019.test.low_goal_bot.Lowbot
import edu.wpi.first.wpilibj.TimedRobot

object TestRobot {
    @JvmStatic
    fun main(args: Array<String>) {
        TimedRobot.startRobot { Lowbot() }
    }
}