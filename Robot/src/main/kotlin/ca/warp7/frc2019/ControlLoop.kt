package ca.warp7.frc2019

import ca.warp7.frc.ControllerState.HeldDown
import ca.warp7.frc.ControllerState.Pressed
import ca.warp7.frc.Controls
import ca.warp7.frc.RobotControlLoop
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.states.DriveState
import ca.warp7.frc2019.states.FrontIntakeState
import ca.warp7.frc2019.states.SuperstructureState
import ca.warp7.frc2019.states.superstructure.WantedPosition
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.FrontIntake
import ca.warp7.frc2019.subsystems.Superstructure

object ControlLoop : RobotControlLoop {

    override fun setup() {
        println("Robot State: Teleop")
    }

    override fun periodic() {

        var overrideDefence = false

        with(Controls.Driver) {

            Drive.set(DriveState.Curvature) {
                xSpeed = leftYAxis
                zRotation = rightXAxis
                isQuickTurn = leftBumper == HeldDown
            }

            if (leftTriggerAxis > ControlConstants.kAxisDeadband) {
                Superstructure.set(SuperstructureState.PassingCargo) {
                    speed = leftTriggerAxis
                    deadband = ControlConstants.kAxisDeadband
                }
            } else if (rightTriggerAxis > ControlConstants.kAxisDeadband) {
                Superstructure.set(SuperstructureState.FeedingCargo) {
                    speed = leftTriggerAxis
                    deadband = ControlConstants.kAxisDeadband
                }
            }

            if (rightBumper == Pressed) overrideDefence = true
            if (backButton == Pressed) Superstructure.set(SuperstructureState.Idle)
            if (startButton == Pressed) Superstructure.set(SuperstructureState.MovingToClimb)
        }

        with(Controls.Operator) {
            if (leftTriggerAxis > ControlConstants.kAxisDeadband) {
                FrontIntake.set(FrontIntakeState.ManualControl) { speed = leftTriggerAxis }
            } else if (rightTriggerAxis > ControlConstants.kAxisDeadband) {
                FrontIntake.set(FrontIntakeState.ManualControl) { speed = leftTriggerAxis * -1 }
            }

            when (Pressed) {
                leftBumper -> SuperstructureState.MovingToPosition.wantedPosition.decreaseSetpoint()
                rightBumper -> SuperstructureState.MovingToPosition.wantedPosition.increaseSetpoint()
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

            if (leftStickButton == HeldDown) {

            }
        }

        if (overrideDefence) {
            Superstructure.set(SuperstructureState.MovingToDefence)
        }
    }
}