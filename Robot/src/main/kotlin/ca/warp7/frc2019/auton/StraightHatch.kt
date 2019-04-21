package ca.warp7.frc2019.auton

import ca.warp7.actionkt.queue
import ca.warp7.actionkt.runOnce
import ca.warp7.actionkt.wait
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.actions.DriveForDistance

object StraightHatch {

    private val io: RobotIO = RobotIO

    val straightHatch
        get() = queue {
            +runOnce { io.grabbing = true }
            +wait(0.5)
            +DriveForDistance((139.5 + 4) / 12 + 1.0)
            //+AlignWithLimelight()
            //+outtakeHatch
            //+DriveForDistance(4.0, isBackwards = true)
        }
}