package ca.warp7.frc2019

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.runAfter
import ca.warp7.frc.ControllerState.HeldDown
import ca.warp7.frc.ControllerState.Pressed
import ca.warp7.frc.set
import ca.warp7.frc.withDriver
import ca.warp7.frc.withOperator
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.constants.SuperstructureConstants
import ca.warp7.frc2019.subsystems.*
import ca.warp7.frc2019.subsystems.drive.DriveState
import ca.warp7.frc2019.subsystems.lift.LiftState
import ca.warp7.frc2019.subsystems.superstructure.PassThrough
import ca.warp7.frc2019.subsystems.superstructure.SuperstructureState

object MainLoop : Action {

    override fun start() {
        println("Robot State: Teleop")
        Drive.set(DriveState.kNeutralOutput)
    }

    override val shouldFinish: Boolean = false

    override fun update() {
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
                    passThroughSpeed = leftTriggerAxis * PassThrough.reverse
                    isOuttaking = true
                    Intake.set {
                        speed = -1 * leftTriggerAxis * SuperstructureConstants.kIntakeSpeedScale
                        extended = true
                    }
                }
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = rightTriggerAxis * PassThrough.forward
                    isOuttaking = rightBumper == HeldDown
                    Intake.set {
                        speed = rightTriggerAxis * SuperstructureConstants.kIntakeSpeedScale
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
        }
        withOperator {
            when {
                leftTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = leftTriggerAxis * PassThrough.reverse
                    isOuttaking = true
                }
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = rightTriggerAxis * PassThrough.forward
                    isOuttaking = true
                }
            }
            when (Pressed) {
                leftBumper -> Unit // TODO Increase setpoint
                rightBumper -> Unit // TODO Decrease setpoint
                bButton -> Unit // TODO Go to hatch setpoint
//                yButton -> Lift.set(LiftState.kPositionOnly) {
//                    setpoint = FieldConstants.secondCargoBayCenterHeightInches
//                }
                xButton -> Outtake.set {
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
//            if (xButton == HeldDown) {
            Lift.set(LiftState.kOpenLoop) { speed = leftYAxis }
//                if (leftStickButton == Pressed) {
//                    LiftMotionPlanner.zeroPosition()
//                }
//            }
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