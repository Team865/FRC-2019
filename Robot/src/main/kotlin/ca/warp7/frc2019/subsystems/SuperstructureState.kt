package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.lift.LiftMotionPlanner
import ca.warp7.frc2019.subsystems.superstructure.IndexingCargo
import ca.warp7.frc2019.subsystems.superstructure.MovingLift

@Suppress("unused")
object SuperstructureState {
    val kIdle = runOnce {}

    val kStartingConfiguration = runOnce {
        LiftMotionPlanner.updateMeasurements(0.0)
    }

    val kDefending = runOnce {
        Lift.set(LiftState.kGoToPosition) { heightInputAbsoluteInches = 0.0 }
        Intake.set(IntakeState.kUp)
        Outtake.set(OuttakeState.kIdle)
    }

    val kIndexingCargo = IndexingCargo

    val kIntakeCargoMode = runOnce {
        Intake.set(IntakeState.kExtendedOpenLoop)
        Lift.set(LiftState.kGoToPosition) {
            heightInputAbsoluteInches = 0.0 // TODO("loading station cargo height")
        }
    }

    val kIntakeHatchMode = runOnce {
        Lift.set(LiftState.kGoToPosition) { heightInputAbsoluteInches = 0.0 }
        Intake.set(IntakeState.kUp)
    }

    val kMovingLift = MovingLift

    val kHoldingPosition = runOnce { }
}