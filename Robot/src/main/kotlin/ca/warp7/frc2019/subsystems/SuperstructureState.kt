package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.superstructure.MoveToPosition
import ca.warp7.frc2019.subsystems.superstructure.PassForward

@Suppress("unused")
object SuperstructureState {
    val kStartingConfiguration = runOnce {
        FrontIntake.set(FrontIntakeState.kIdle)
        Lift.set(LiftState.kIdle)
        BackIntake.set(BackIntakeState.kRetracting)
        LED.set(LEDState.kOff)

        Superstructure.compressor.closedLoopControl = false
    }

    val kManual = runOnce { }

    val kIdle
        get() = runOnce {

        }


    val kPassingCargo = PassForward

    val kMovingToClimb
        get() = runOnce {
            BackIntake.set(BackIntakeState.LiftingRobot)
        }

    val kMovingToPosition = MoveToPosition
}