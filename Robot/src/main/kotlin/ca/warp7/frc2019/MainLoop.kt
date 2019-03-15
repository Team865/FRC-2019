package ca.warp7.frc2019

import ca.warp7.actionkt.Action
import ca.warp7.frc.ControllerState.*
import ca.warp7.frc.set
import ca.warp7.frc.withDriver
import ca.warp7.frc.withOperator
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.constants.HatchCargo
import ca.warp7.frc2019.constants.SuperstructureConstants
import ca.warp7.frc2019.subsystems.*
import ca.warp7.frc2019.subsystems.drive.DriveState
import ca.warp7.frc2019.subsystems.lift.LiftMotionPlanner
import ca.warp7.frc2019.subsystems.lift.LiftState
import ca.warp7.frc2019.subsystems.superstructure.SuperstructureState
import kotlin.math.absoluteValue

object MainLoop : Action {

    override fun start() {
        println("Robot State: Teleop")
        Drive.set(DriveState.kNeutralOutput)
        Limelight.set { isDriver = true }
    }

    override val shouldFinish: Boolean = false

    override fun update() {
        var passThroughSpeed = 0.0
        var isOuttaking = false
        var fastOuttake = false
        withDriver {
            Drive.set(DriveState.kAlignedCurvature) {
                xSpeed = leftYAxis * -1
                zRotation = rightXAxis
                isQuickTurn = leftBumper == HeldDown
                isAligning = yButton == HeldDown
            }
            if (xButton == Pressed) Limelight.isDriver = !Limelight.isDriver
            if (yButton == Pressed) Limelight.isDriver = false
            else if (yButton == Released) Limelight.isDriver = true
            when {
                leftTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = -1 * leftTriggerAxis
                    isOuttaking = true
                    Intake.set { speed = -1 * leftTriggerAxis * SuperstructureConstants.kIntakeSpeedScale }
                }
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = rightTriggerAxis
                    isOuttaking = rightBumper == HeldDown
                    Intake.set { speed = rightTriggerAxis * SuperstructureConstants.kIntakeSpeedScale }
                }
                else -> {
                    Intake.set { speed = 0.0 }
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
            if (leftYAxis.absoluteValue > ControlConstants.kLiftControlDeadband) {
                Lift.set(LiftState.kOpenLoop) { speed = leftYAxis }
            } else LiftState.kOpenLoop.speed = 0.0
            when (Pressed) {
                rightBumper -> LiftMotionPlanner.increaseSetpoint()
                leftBumper -> LiftMotionPlanner.decreaseSetpoint()
                yButton -> {
                    LiftMotionPlanner.setpointType = HatchCargo.Hatch
                    Lift.set(LiftState.kPositionOnly) { setpoint = LiftMotionPlanner.getCoolSetpoint() }
                }
                bButton -> {
                    LiftMotionPlanner.setpointType = HatchCargo.Cargo
                    Lift.set(LiftState.kPositionOnly) { setpoint = LiftMotionPlanner.getCoolSetpoint() }
                }
                xButton -> Outtake.set {
                    grabbing = !grabbing
                    pushing = false
                }
                aButton -> Outtake.set {
                    pushing = !pushing
                    grabbing = false
                }
                else -> Unit
            }
            fastOuttake = rightBumper == HeldDown
        }
        if (passThroughSpeed != 0.0) {
            Superstructure.set(SuperstructureState.kPassThrough) {
                speed = passThroughSpeed
                outtaking = isOuttaking
            }
        } else {
            Superstructure.set(SuperstructureState.kIdle)
        }
        if (fastOuttake) {
            Outtake.speed = 1.0
            Outtake.grabbing = true
        }
    }
}