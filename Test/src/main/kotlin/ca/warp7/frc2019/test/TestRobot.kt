package ca.warp7.frc2019.test

import ca.warp7.frc2019.test.commons.LimelightTest
import ca.warp7.frc2019.test.drive.*
import ca.warp7.frc2019.test.infrastructure.SolenoidsTest
import ca.warp7.frc2019.test.lift.LiftPID
import edu.wpi.first.wpilibj.TimedRobot

object TestRobot {
    @JvmStatic
    fun main(args: Array<String>) {
        TimedRobot.startRobot { LiftPID() }
    }
}