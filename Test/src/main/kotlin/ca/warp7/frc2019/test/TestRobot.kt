package ca.warp7.frc2019.test

import ca.warp7.frc2019.test.lift_outtake.tuning.LiftOuttakeTuning
import edu.wpi.first.wpilibj.TimedRobot

object TestRobot {
    @JvmStatic
    fun main(args: Array<String>) {
        TimedRobot.startRobot { LiftOuttakeTuning() }
    }
}