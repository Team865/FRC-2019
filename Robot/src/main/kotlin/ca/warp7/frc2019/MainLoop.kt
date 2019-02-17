package ca.warp7.frc2019

import ca.warp7.frc.ControllerState.HeldDown
import ca.warp7.frc.ControllerState.Pressed
import ca.warp7.frc.RobotControlLoop
import ca.warp7.frc.set
import ca.warp7.frc.withDriver
import ca.warp7.frc.withOperator
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.subsystems.*
import ca.warp7.frc2019.subsystems.drive.DriveState
import ca.warp7.frc2019.subsystems.lift.LiftState
import ca.warp7.frc2019.subsystems.superstructure.SuperstructureState

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
            when {
                leftTriggerAxis > ControlConstants.kControlDeadband -> {
                    Superstructure.set(SuperstructureState.kPassThrough) {
                        speed = leftTriggerAxis * forward
                        outtaking = rightBumper == HeldDown
                    }
                    Intake.set {
                        speed = leftTriggerAxis
                        extended = true
                    }
                }
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    Superstructure.set(SuperstructureState.kPassThrough) {
                        speed = rightTriggerAxis * reverse
                        outtaking = true
                    }
                    Intake.set {
                        speed = -rightTriggerAxis
                        extended = true
                    }
                }
                else -> Intake.set {
                    speed = 0.0
                    extended = false
                }
            }
            if (aButton == Pressed) Climber.set { climbing = !climbing }
            if (startButton == Pressed) Superstructure.set(SuperstructureState.kDefending)
        }
        withOperator {
            when {
                leftTriggerAxis > ControlConstants.kControlDeadband ->
                    Superstructure.set(SuperstructureState.kPassThrough) {
                        speed = leftTriggerAxis * forward
                        outtaking = true
                    }
                rightTriggerAxis > ControlConstants.kControlDeadband ->
                    Superstructure.set(SuperstructureState.kPassThrough) {
                        speed = rightTriggerAxis * reverse
                        outtaking = true
                    }
            }
            when (Pressed) {
                leftBumper -> Unit // TODO Increase setpoint
                rightBumper -> Unit // TODO Decrease setpoint
                bButton -> Unit // TODO Go to hatch setpoint
                yButton -> Unit // TODO Go to cargo setpoint
                aButton -> Unit // TODO toggle hatch
                else -> Unit
            }
            if (xButton == HeldDown) Lift.set(LiftState.kOpenLoop) { speed = leftYAxis }
            if (startButton == Pressed) Superstructure.set(SuperstructureState.kDefending)
        }
    }
}