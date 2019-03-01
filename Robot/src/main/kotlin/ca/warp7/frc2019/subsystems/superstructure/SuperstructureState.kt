package ca.warp7.frc2019.subsystems.superstructure

import ca.warp7.actionkt.runOnce
import ca.warp7.frc.set
import ca.warp7.frc2019.subsystems.Conveyor
import ca.warp7.frc2019.subsystems.Intake
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.Outtake
import ca.warp7.frc2019.subsystems.lift.LiftState

object SuperstructureState {

    @Suppress("unused")
    val kDefending = runOnce {
        Intake.set { extended = false }
        Lift.set(LiftState.kOpenLoop) { speed = 0.0 }
        Outtake.set { speed = 0.0 }
        Conveyor.set { speed = 0.0 }
    }

    val kPassThrough = PassThrough

    val kIdle = runOnce {
        Outtake.set {
            speed = 0.0
            //FIXME grabbing = false
            //FIXME pushing = false
        }
        Conveyor.set { speed = 0.0 }
    }
}