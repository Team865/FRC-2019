package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc.set
import ca.warp7.frc2019.subsystems.lift.LiftMotionPlanner
import ca.warp7.frc2019.subsystems.superstructure.MovingLift
import ca.warp7.frc2019.subsystems.superstructure.PassThrough

@Suppress("unused")
object SuperstructureState {
    val kIdle = runOnce {}

    val kStartingConfiguration = runOnce {
        LiftMotionPlanner.updateMeasurements(0.0)
    }

    val kDefending = runOnce {
        Intake.set { extended = false }
        Lift.set(LiftState.kOpenLoop) { speed = 0.0 }
        Outtake.set { speed = 0.0 }
        Conveyor.set { speed = 0.0 }
    }

    val kPassThrough = PassThrough

    val kIntakeCargoMode = runOnce {
        Intake.set(IntakeState.kExtendedOpenLoop)
        Lift.set(LiftState.kGoToPosition) {
            heightInputAbsoluteInches = 0.0 // TODO("loading station cargo height")
        }
    }

    val kIntakeHatchMode = runOnce {
        Lift.set(LiftState.kGoToPosition) { heightInputAbsoluteInches = 0.0 }
        Intake.set { extended = false }
    }

    val kMovingLift = MovingLift

    val kHoldingPosition = runOnce { }
}