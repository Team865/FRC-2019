package ca.warp7.frc2019

import ca.warp7.frc.ControllerState.HeldDown
import ca.warp7.frc.ControllerState.Pressed
import ca.warp7.frc.Controls
import ca.warp7.frc.RobotControlLoop
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.states.DriveState
import ca.warp7.frc2019.states.FrontIntakeState
import ca.warp7.frc2019.states.LiftState
import ca.warp7.frc2019.states.SuperstructureState
import ca.warp7.frc2019.states.superstructure.WantedPosition
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.FrontIntake
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.Superstructure

object MainLoop : RobotControlLoop {

    override fun setup() {
        println("Robot State: Teleop")
        Drive.set(DriveState.NeutralOutput)
        Superstructure.set(SuperstructureState.MovingToPosition) {
            wantedPosition.positionType = WantedPosition.PositionType.Normal
        }
    }

    override fun periodic() {

        Controls.Driver.apply {

            Drive.set(DriveState.Curvature) {
                xSpeed = leftYAxis
                zRotation = rightXAxis
                isQuickTurn = leftBumper == HeldDown
            }

            if (leftTriggerAxis > ControlConstants.kAxisDeadband) {
                Superstructure.set(SuperstructureState.PassingCargo) { speed = leftTriggerAxis }
            } else if (rightTriggerAxis > ControlConstants.kAxisDeadband) {
                Superstructure.set(SuperstructureState.PassingCargo) { speed = leftTriggerAxis * -1 }
            }

            if (backButton == Pressed) Superstructure.set(SuperstructureState.Idle)
            if (startButton == Pressed) Superstructure.set(SuperstructureState.MovingToClimb)
        }

        Controls.Operator.apply {
            if (leftTriggerAxis > ControlConstants.kAxisDeadband) {
                FrontIntake.set(FrontIntakeState.ManualControl) { speed = leftTriggerAxis }
            } else if (rightTriggerAxis > ControlConstants.kAxisDeadband) {
                FrontIntake.set(FrontIntakeState.ManualControl) { speed = leftTriggerAxis * -1 }
            }

            when (Pressed) {
                leftBumper -> SuperstructureState.MovingToPosition.wantedPosition.decreaseLiftSetpoint()
                rightBumper -> SuperstructureState.MovingToPosition.wantedPosition.increaseLiftSetpoint()
                aButton -> Superstructure.set(SuperstructureState.MovingToPosition) {
                    wantedPosition.setpointType = WantedPosition.SetpointType.Cargo
                }
                bButton -> Superstructure.set(SuperstructureState.MovingToPosition) {
                    wantedPosition.setpointType = WantedPosition.SetpointType.HatchPanel
                }
                xButton -> TODO("Toggle the secondary intake to hold or release grip on the hatch panel")
                yButton -> TODO("Outtake the hatch panel by releasing the grip and push out with piston")
                else -> {
                }
            }

            if (rightStickButton == HeldDown) {
                Lift.set(LiftState.Idle)
            }

            if (startButton == Pressed) {
                Superstructure.set(SuperstructureState.MovingToPosition) {
                    wantedPosition.positionType = when (wantedPosition.positionType) {
                        WantedPosition.PositionType.Normal -> WantedPosition.PositionType.Defending
                        WantedPosition.PositionType.Defending -> WantedPosition.PositionType.Normal
                        WantedPosition.PositionType.Climbing -> WantedPosition.PositionType.Climbing
                    }
                }
            }
        }
    }
}