package ca.warp7.frc2019.states

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.*

@Suppress("unused")
object SuperstructureState {
    val StartingConfiguration
        get() = runOnce {
            FrontIntake.set(FrontIntakeState.Idle)
            HatchIntake.set(HatchIntakeState.Retracting)
            Lift.set(LiftState.Idle)
            BackIntake.set(BackIntakeState.Retracting)
            Conveyor.set(ConveyorState.Idle)
            LED.set(LEDState.Off)

            Superstructure.compressor.closedLoopControl = false
        }

    val Defending
        get() = runOnce {

        }

    val ClimbingSecondLevel
        get() = runOnce {
            BackIntake.set(BackIntakeState.LiftingRobot)
        }
}