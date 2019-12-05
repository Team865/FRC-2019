package ca.warp7.frc2019.auton

import ca.warp7.frc.action.runOnce
import ca.warp7.frc.action.sequential
import ca.warp7.frc.action.wait
import ca.warp7.frc.action.withTimeout
import ca.warp7.frc2019.actions.LiftSetpoint
import ca.warp7.frc2019.actions.driveStraight
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance

object SubActions {
    private val io: BaseIO = ioInstance()

    internal fun intakeHatch() = sequential {
        +runOnce { io.grabbing = true }
        +wait(0.3)
        +LiftSetpoint(FieldConstants.kCargo1Height).withTimeout(0.4)
        +wait(0.1)
        +driveStraight(10.0 / 12, isBackwards = true)
        +LiftSetpoint(LiftConstants.kHomeHeightInches).withTimeout(0.4)
    }

    internal fun outtakeHatch() = sequential {
        +runOnce { io.grabbing = false }
        +runOnce { io.pushing = true }
        +wait(0.5)
        +runOnce { io.pushing = false }
    }
}