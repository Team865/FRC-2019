package ca.warp7.frc2019

import ca.warp7.actionkt.Action
import ca.warp7.frc.*
import ca.warp7.frc.ControllerState.HeldDown
import ca.warp7.frc.ControllerState.Pressed
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.constants.HatchCargo
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.constants.SuperstructureConstants
import ca.warp7.frc2019.subsystems.*
import ca.warp7.frc2019.subsystems.drive.DriveState
import ca.warp7.frc2019.subsystems.lift.LiftState
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
            if (xButton == Pressed) Limelight.isDriver = !Limelight.isDriver
            if(yButton==HeldDown) Limelight.isDriver = false
/*                        if (yButton == HeldDown) {
                Drive.set(DriveState.kTurnPID)
                Drive.set(DriveState.kCurveToTarget) {
                    xSpeed = leftYAxis * -1
                    zRotation = rightXAxis
                    isQuickTurn = leftBumper == HeldDown
                }
            } else {
                Drive.set(DriveState.kCurvature) {
                    xSpeed = leftYAxis * -1
                    zRotation = rightXAxis
                    isQuickTurn = leftBumper == HeldDown
                }
            }*/
            Drive.set(DriveState.kCurveToTarget) {
                xSpeed = leftYAxis * -1
                zRotation = rightXAxis
                isQuickTurn = leftBumper == HeldDown
                isAligning = yButton == HeldDown
            }
            when {
                leftTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = -1 * leftTriggerAxis
                    isOuttaking = true
                    Intake.set {
                        speed = -1 * leftTriggerAxis * SuperstructureConstants.kIntakeSpeedScale
                        extended = true
                    }
                }
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = rightTriggerAxis
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
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = rightTriggerAxis
                    isOuttaking = true
                }
                leftTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = -1 * leftTriggerAxis
                    isOuttaking = true
                }
            }

            if (!leftYAxis.epsilonEquals(0.0, 0.1)) {
                Lift.set(LiftState.kOpenLoop) { speed = leftYAxis }
            } else {
                LiftState.kOpenLoop.speed = 0.0
            }

            when (Pressed) {
                /*
                rightBumper -> {
                    Lift.setpointLevel += when {
                        Lift.setpointLevel < 2 -> 1
                        else -> 0
                    }
                    Lift.set(LiftState.kPositionOnly) { setpoint = Lift.coolSetpoint }
                }
                leftBumper -> {
                    Lift.setpointLevel -= when {
                        Lift.setpointLevel > 0 -> 1
                        else -> 0
                    }
                    Lift.set(LiftState.kPositionOnly) { setpoint = Lift.coolSetpoint }

                }

                yButton -> {
                    Lift.setpointType = HatchCargo.Hatch
                    Lift.set(LiftState.kPositionOnly) { setpoint = Lift.coolSetpoint }
                    println("Cool ${Lift.coolSetpoint}")
                }
                bButton -> {
                    Lift.setpointType = HatchCargo.Cargo
                    Lift.set(LiftState.kPositionOnly) { setpoint = Lift.coolSetpoint }
                    println("Cool ${Lift.coolSetpoint}")
                }
                */
                bButton -> Outtake.set {
                    grabbing = !grabbing
                    pushing = false
                }
                aButton -> Outtake.set {
                    pushing = !pushing
                    grabbing = false
                }

                else -> Unit
            }
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