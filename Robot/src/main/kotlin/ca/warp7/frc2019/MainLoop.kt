package ca.warp7.frc2019

import ca.warp7.actionkt.Action
import ca.warp7.frc.ControllerState.*
import ca.warp7.frc.set
import ca.warp7.frc.withDriver
import ca.warp7.frc.withOperator
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.constants.HatchCargo
import ca.warp7.frc2019.constants.LiftConstants
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
        Outtake.set {
            grabbing = true
            pushing = false
        }
    }

    override val shouldFinish: Boolean = false

    var isStopOverrideOuttake = false

    override fun update() {
        var passThroughSpeed = 0.0
        var isOpenOuttake = false
        var isFastOuttake = false
        withDriver {
            Drive.set(DriveState.kAlignedCurvature) {
                xSpeed = leftYAxis * -1
                zRotation = rightXAxis
                isQuickTurn = leftBumper == HeldDown
                isAligning = rightBumper == HeldDown
            }
            if (bButton == Pressed) Limelight.isDriver = !Limelight.isDriver
            when (rightBumper) {
                Pressed -> Limelight.isDriver = false
                Released -> Limelight.isDriver = false
                else -> Unit
            }
            when {
                leftTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = -leftTriggerAxis
                    isOpenOuttake = true
                    Intake.set { speed = -leftTriggerAxis * SuperstructureConstants.kIntakeSpeedScale }
                    isStopOverrideOuttake = false
                }
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = rightTriggerAxis
                    isOpenOuttake = rightBumper == HeldDown
                    Intake.set { speed = rightTriggerAxis * SuperstructureConstants.kIntakeSpeedScale }
                    isStopOverrideOuttake = false
                }
                else -> {
                    Intake.set { speed = 0.0 }
                }
            }
            when (Pressed) {
                xButton -> Outtake.set {
                    grabbing = !grabbing
                    pushing = false
                    isStopOverrideOuttake = true
                }
                aButton -> Outtake.set {
                    pushing = !pushing
                    grabbing = false
                    isStopOverrideOuttake = true
                }
                else -> Unit
            }
            isFastOuttake = bButton == Pressed
        }
        withOperator {
            when {
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = rightTriggerAxis
                    isOpenOuttake = true
                    isStopOverrideOuttake = false
                }
                leftTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = -leftTriggerAxis
                    isOpenOuttake = true
                    isStopOverrideOuttake = false
                }
            }
            if (leftYAxis.absoluteValue > ControlConstants.kLiftControlDeadband) {
                Lift.set(LiftState.kOpenLoop) { speed = leftYAxis }
            } else {
                LiftState.kOpenLoop.speed = 0.0
            }
            when (Pressed) {
                rightBumper -> LiftMotionPlanner.increaseSetpoint()
                leftBumper -> LiftMotionPlanner.decreaseSetpoint()
                xButton -> Lift.set(LiftState.kGoToSetpoint) { setpoint = LiftConstants.kHomeHeightInches }
                yButton -> {
                    LiftMotionPlanner.setpointType = HatchCargo.Hatch
                    Lift.set(LiftState.kGoToSetpoint) { setpoint = LiftMotionPlanner.getCoolSetpoint() }
                }
                bButton -> {
                    LiftMotionPlanner.setpointType = HatchCargo.Cargo
                    Lift.set(LiftState.kGoToSetpoint) { setpoint = LiftMotionPlanner.getCoolSetpoint() }
                }
                else -> Unit
            }
        }
        Superstructure.set(SuperstructureState.kPassThrough) {
            speed = passThroughSpeed
            openOuttake = isOpenOuttake
            fastOuttake = isFastOuttake
            stopOverrideOuttake = isStopOverrideOuttake
        }
    }
}