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
    }

    override fun periodic() {

        Controls.robotDriver.apply {

            Drive.set(DriveState.kCurvature) {
                xSpeed = leftYAxis
                zRotation = rightXAxis
                isQuickTurn = leftBumper == HeldDown
            }

            if (leftTriggerAxis > ControlConstants.kAxisDeadband) {
                Superstructure.set(SuperstructureState.kIndexingCargo) { speedScale = leftTriggerAxis }
            } else if (rightTriggerAxis > ControlConstants.kAxisDeadband) {
                Superstructure.set(SuperstructureState.kIndexingCargo) { speedScale = leftTriggerAxis * -1 }
            }

            if (startButton == Pressed) {
                // TODO Reserved for climbing mechanism
            }
        }

        Controls.robotOperator.apply {
            when {
                leftTriggerAxis > ControlConstants.kAxisDeadband ->
                    Superstructure.set(SuperstructureState.kIndexingCargo) { setOverride(leftTriggerAxis) }
                rightTriggerAxis > ControlConstants.kAxisDeadband ->
                    Superstructure.set(SuperstructureState.kIndexingCargo) { setOverride(leftTriggerAxis * -1) }
                else -> SuperstructureState.kIndexingCargo.isOverride = false
            }

            when (Pressed) {
                leftBumper -> SuperstructureState.kMovingLift.wantedPosition.decreaseLiftSetpoint()
                rightBumper -> SuperstructureState.kMovingLift.wantedPosition.increaseLiftSetpoint()
                yButton -> Superstructure.set(SuperstructureState.kMovingLift) {
                    wantedPosition.setpointType = WantedPosition.SetpointType.Cargo
                }
                bButton -> Superstructure.set(SuperstructureState.kMovingLift) {
                    wantedPosition.setpointType = WantedPosition.SetpointType.HatchPanel
                }
                else -> Unit
            }

            if (rightStickButton == HeldDown) {
                Lift.set(LiftState.kIdle)
            }

            if (startButton == Pressed) {
                Superstructure.set(SuperstructureState.kDefending)
            }
        }
    }
}