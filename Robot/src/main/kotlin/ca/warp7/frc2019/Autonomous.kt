package ca.warp7.frc2019

import ca.warp7.actionkt.future
import ca.warp7.actionkt.queue
import ca.warp7.actionkt.runOnce
import ca.warp7.actionkt.wait
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Outtake
import ca.warp7.frc2019.subsystems.drive.DriveState
import ca.warp7.frc2019.subsystems.drive.LinearDriveTrajectory

@Suppress("unused")
object Autonomous {

    val mode get() = nothingMode

    private val nothingMode = { runOnce { } }

    private val straightHatch
        get() = queue {
            +Drive.future(DriveState.kNeutralOutput)
            +runOnce { Outtake.grabbing = true }
            +LinearDriveTrajectory(8.0)
            +runOnce { Outtake.grabbing = false }
            +wait(0.1)
            +runOnce { Outtake.pushing = true }
            +wait(0.3)
            +runOnce { Outtake.pushing = false }
        }
}