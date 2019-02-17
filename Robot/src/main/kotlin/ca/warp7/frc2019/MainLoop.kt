package ca.warp7.frc2019

import ca.warp7.actionkt.runAfter
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
import ca.warp7.frc2019.subsystems.superstructure.PassThrough
import ca.warp7.frc2019.subsystems.superstructure.SuperstructureState

object MainLoop : RobotControlLoop {

    override fun setup() {
        println("Robot State: Teleop")
        Drive.set(DriveState.kNeutralOutput)
    }

    override fun periodic() {
        var passThroughSpeed = 0.0
        var isOuttaking = false
        withDriver {
            Drive.set(DriveState.kCurvature) {
                xSpeed = leftYAxis
                zRotation = rightXAxis
                isQuickTurn = leftBumper == HeldDown
            }
            when {
                leftTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = leftTriggerAxis * PassThrough.forward
                    isOuttaking = rightBumper == HeldDown
                    Intake.set {
                        speed = leftTriggerAxis * speedScale
                        extended = true
                    }
                }
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = leftTriggerAxis * PassThrough.reverse
                    isOuttaking = true
                    Intake.set {
                        speed = -rightTriggerAxis * speedScale
                        extended = true
                    }
                }
                else -> {
                    Intake.set {
                        speed = 0.0
                        extended = false
                    }
                }
            }
            if (startButton == Pressed) Climber.set { climbing = !climbing }
        }
        withOperator {
            when {
                leftTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = leftTriggerAxis * PassThrough.forward
                    isOuttaking = true
                }
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = leftTriggerAxis * PassThrough.forward
                    isOuttaking = true
                }
            }
            when (Pressed) {
                leftBumper -> Unit // TODO Increase setpoint
                rightBumper -> Unit // TODO Decrease setpoint
                bButton -> Unit // TODO Go to hatch setpoint
                yButton -> Unit // TODO Go to cargo setpoint
                aButton -> Outtake.set {
                    if (grabbing) {
                        grabbing = false
                        pushing = true
                        set(runAfter(0.5) { pushing = false })
                    } else {
                        grabbing = true
                        pushing = false
                    }
                }
                else -> Unit
            }
            if (xButton == HeldDown) Lift.set(LiftState.kOpenLoop) { speed = leftYAxis }
            else Lift.set(LiftState.kIdle)
            if (startButton == Pressed) Superstructure.set(SuperstructureState.kDefending)
        }
        if (passThroughSpeed != 0.0) {
            Superstructure.set(SuperstructureState.kPassThrough) {
                speed = passThroughSpeed
                outtaking = isOuttaking
            }
        } else {
            Superstructure.set(SuperstructureState.kIdle)
        }
    }
}