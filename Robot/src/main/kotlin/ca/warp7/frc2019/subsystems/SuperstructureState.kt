package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.superstructure.IndexingCargo
import ca.warp7.frc2019.subsystems.superstructure.MovingLift

@Suppress("unused")
object SuperstructureState {
    val kIdle = runOnce {}

    val kStartingConfiguration = runOnce {
        Outtake.set(OuttakeState.kIdle)
        Lift.set(LiftState.kIdle)
        Intake.set(IntakeState.kUp)
        Conveyor.set(ConveyorState.kIdle)
    }

    val kDefending = runOnce {
        Lift.set(LiftState.kGoToPosition) { heightFromHome = 0.0 }
        Intake.set(IntakeState.kUp)
        Outtake.set(OuttakeState.kIdle)
    }

    val kIndexingCargo = IndexingCargo

    val kIntakeCargoMode = runOnce {
        Intake.set(IntakeState.kStartOpenLoop)
        Lift.set(LiftState.kGoToPosition) {
            heightFromHome = 0.0 // TODO("loading station cargo height")
        }
    }

    val kIntakeHatchMode = runOnce {
        Lift.set(LiftState.kGoToPosition) { heightFromHome = 0.0 }
        Intake.set(IntakeState.kUp)
    }

    val kMovingLift = MovingLift

    val kHoldingPosition = runOnce { }
}