package ca.warp7.frc2019

import ca.warp7.actionkt.runOnce
import ca.warp7.frc.ControllerState.HeldDown
import ca.warp7.frc.ControllerState.Pressed
import ca.warp7.frc.RobotControlLoop
import ca.warp7.frc.withDriver
import ca.warp7.frc.withOperator
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.subsystems.*
import ca.warp7.frc2019.subsystems.superstructure.LiftSetpointType

object MainLoop : RobotControlLoop {

    override fun setup() {
        println("Robot State: Teleop")
        Drive.set(DriveState.kNeutralOutput)
    }

    override fun periodic() {
        withDriver {
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
            if (aButton == Pressed) {
                Climber.set(Climber.runOnce { climbing = !climbing })
            }
        }
        withOperator {
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
                    wantedPosition.setpointType = LiftSetpointType.Cargo
                }
                bButton -> {
                    Superstructure.set(SuperstructureState.kMovingLift) {
                        wantedPosition.setpointType = LiftSetpointType.Hatch
                    }
                }
                aButton -> Hatch.set(HatchState.kPushing)
                else -> Unit
            }
            if (rightStickButton == HeldDown) {
                Lift.set(LiftState.kOpenLoop) { speed = leftYAxis }
            }
            if (startButton == Pressed) {
                Superstructure.set(SuperstructureState.kDefending)
            }
        }
    }
}