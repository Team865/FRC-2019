package ca.warp7.frc2019

import ca.warp7.actionkt.queue
import ca.warp7.actionkt.runOnce
import ca.warp7.actionkt.wait
import ca.warp7.frc2019.subsystems.Outtake
import ca.warp7.frc2019.subsystems.drive.DriveForDistance
import ca.warp7.frc2019.subsystems.drive.QuickTurn

@Suppress("unused")
object Autonomous {

    val mode get() = nothingMode

    private val nothingMode = { runOnce { } }

    private val straightHatch
        get() = queue {
            +runOnce { Outtake.grabbing = true }
            +DriveForDistance(12.0)
            +runOnce { Outtake.grabbing = false }
            +wait(0.1)
            +runOnce { Outtake.pushing = true }
            +wait(0.3)
            +runOnce { Outtake.pushing = false }
        }

    private val leftSideCargoShipHatch
        get() = queue {
            +runOnce { Outtake.grabbing = true }
            +DriveForDistance(12.0)
            +QuickTurn(90.0)
            +DriveForDistance(2.0)
            +runOnce { Outtake.grabbing = false }
            +wait(0.1)
            +runOnce { Outtake.pushing = true }
            +wait(0.3)
            +runOnce { Outtake.pushing = false }
        }

    private val leftSideRocketLevel1Hatch
        get() = queue {
            +runOnce { Outtake.grabbing = true }
            +DriveForDistance(5.0)
            +QuickTurn(-30.0)
            +DriveForDistance(10.0)
            +runOnce { Outtake.grabbing = false }
            +wait(0.1)
            +runOnce { Outtake.pushing = true }
            +wait(0.3)
            +runOnce { Outtake.pushing = false }
        }
}