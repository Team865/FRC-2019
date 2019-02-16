package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc.set
import ca.warp7.frc2019.subsystems.superstructure.PassThrough

object SuperstructureState {

    val kDefending = runOnce {
        Intake.set { extended = false }
        Lift.set(LiftState.kOpenLoop) { speed = 0.0 }
        Outtake.set { speed = 0.0 }
        Conveyor.set { speed = 0.0 }
    }

    val kPassThrough = PassThrough
}