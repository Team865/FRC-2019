package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.superstructure.MoveToPosition
import ca.warp7.frc2019.subsystems.superstructure.PassBack
import ca.warp7.frc2019.subsystems.superstructure.PassForward

@Suppress("unused")
object SuperstructureState {
    val StartingConfiguration = runOnce {
        FrontIntake.set(FrontIntakeState.Idle)
        HatchIntake.set(HatchIntakeState.Retracting)
        Lift.set(LiftState.Idle)
        BackIntake.set(BackIntakeState.Retracting)
        LED.set(LEDState.Off)

        Superstructure.compressor.closedLoopControl = false
    }

    val Manual = runOnce { }

    val MovingToDefence
        get() = runOnce {

        }

    val Idle
        get() = runOnce {

        }


    val Defending
        get() = runOnce {

        }

    val PassingCargo = PassForward

    val FeedingCargo = PassBack

    val MovingToClimb
        get() = runOnce {
            BackIntake.set(BackIntakeState.LiftingRobot)
        }

    val MovingToPosition = MoveToPosition
}