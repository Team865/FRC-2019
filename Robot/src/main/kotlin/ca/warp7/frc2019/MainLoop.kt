package ca.warp7.frc2019

import ca.warp7.frc.ControllerState.HeldDown
import ca.warp7.frc.ControllerState.Pressed
import ca.warp7.frc.Controls
import ca.warp7.frc.RobotControlLoop
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.subsystems.*
import ca.warp7.frc2019.subsystems.superstructure.WantedPosition

object MainLoop : RobotControlLoop {

    override fun setup() {
        println("Robot State: Teleop")
        Drive.set(DriveState.kNeutralOutput)
        Superstructure.set(SuperstructureState.kMovingToPosition) {
            wantedPosition.positionType = WantedPosition.PositionType.Normal
        }
    }

    override fun periodic() {

        Controls.robotDriver.apply {

            Drive.set(DriveState.kCurvature) {
                xSpeed = leftYAxis
                zRotation = rightXAxis
                isQuickTurn = leftBumper == HeldDown
            }

            if (leftTriggerAxis > ControlConstants.kAxisDeadband) {
                Superstructure.set(SuperstructureState.kPassingCargo) { speed = leftTriggerAxis }
            } else if (rightTriggerAxis > ControlConstants.kAxisDeadband) {
                Superstructure.set(SuperstructureState.kPassingCargo) { speed = leftTriggerAxis * -1 }
            }

            if (startButton == Pressed) Superstructure.set(SuperstructureState.kMovingToClimb)
        }

        Controls.robotOperator.apply {
            if (leftTriggerAxis > ControlConstants.kAxisDeadband) {
                FrontIntake.set(FrontIntakeState.ManualControl) { speed = leftTriggerAxis }
            } else if (rightTriggerAxis > ControlConstants.kAxisDeadband) {
                FrontIntake.set(FrontIntakeState.ManualControl) { speed = leftTriggerAxis * -1 }
            }

            when (Pressed) {
                leftBumper -> SuperstructureState.kMovingToPosition.wantedPosition.decreaseLiftSetpoint()
                rightBumper -> SuperstructureState.kMovingToPosition.wantedPosition.increaseLiftSetpoint()
                aButton -> Superstructure.set(SuperstructureState.kMovingToPosition) {
                    wantedPosition.setpointType = WantedPosition.SetpointType.Cargo
                }
                bButton -> Superstructure.set(SuperstructureState.kMovingToPosition) {
                    wantedPosition.setpointType = WantedPosition.SetpointType.HatchPanel
                }
                xButton -> TODO("Toggle the secondary intake to hold or release grip on the hatch panel")
                yButton -> TODO("Outtake the hatch panel by releasing the grip and push out with piston")
                else -> {
                }
            }

            if (rightStickButton == HeldDown) {
                Lift.set(LiftState.kIdle)
            }

            if (startButton == Pressed) {
                Superstructure.set(SuperstructureState.kMovingToPosition) {
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