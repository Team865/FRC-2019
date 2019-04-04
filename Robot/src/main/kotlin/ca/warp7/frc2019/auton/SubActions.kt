package ca.warp7.frc2019.auton

import ca.warp7.actionkt.queue
import ca.warp7.actionkt.runOnce
import ca.warp7.actionkt.wait
import ca.warp7.actionkt.withTimeout
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Outtake
import ca.warp7.frc2019.subsystems.drive.DriveForDistance
import ca.warp7.frc2019.subsystems.lift.GoToSetpoint

object SubActions {

    internal val intakeHatch
        get() = queue {
            +runOnce { Outtake.grabbing = true }
            +wait(0.3)
            +GoToSetpoint(FieldConstants.kCargo1Height).withTimeout(0.4)
            +wait(0.1)
            +DriveForDistance(10.0 / 12, isBackwards = true)
            +GoToSetpoint(LiftConstants.kHomeHeightInches).withTimeout(0.4)
        }

    internal val outtakeHatch
        get() = queue {
            +runOnce { Outtake.grabbing = false }
            +runOnce { Outtake.pushing = true }
            +wait(0.5)
            +runOnce { Outtake.pushing = false }
        }
}