package ca.warp7.frc2019.auton

import ca.warp7.actionkt.queue
import ca.warp7.actionkt.runOnce
import ca.warp7.actionkt.wait
import ca.warp7.frc2019.subsystems.Outtake
import ca.warp7.frc2019.subsystems.drive.DriveForDistance

object StraightHatch {

    val straightHatch
        get() = queue {
            +runOnce { Outtake.grabbing = true }
            +wait(0.5)
            +DriveForDistance((139.5 + 4) / 12 + 1.0)
            //+AlignWithLimelight()
            //+outtakeHatch
            //+DriveForDistance(4.0, isBackwards = true)
        }
}