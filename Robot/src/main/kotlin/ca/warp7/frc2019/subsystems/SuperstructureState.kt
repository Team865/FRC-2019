package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.superstructure.MoveToPosition
import ca.warp7.frc2019.subsystems.superstructure.PassForward

@Suppress("unused")
object SuperstructureState {
    val kStartingConfiguration = runOnce {
        Outtake.set(OuttakeState.kIdle)
        Lift.set(LiftState.kIdle)
        Intake.set(IntakeState.kRetracting)
        LED.set(LEDState.kOff)
    }

    val kManual = runOnce { }

    val kIdle = runOnce {

        }


    val kPassingCargo = PassForward

    val kMovingToClimb
        get() = runOnce {
            Intake.set(IntakeState.LiftingRobot)
        }

    val kMovingToPosition = MoveToPosition
}