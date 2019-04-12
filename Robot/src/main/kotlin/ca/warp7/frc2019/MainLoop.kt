package ca.warp7.frc2019

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.action
import ca.warp7.actionkt.queue
import ca.warp7.actionkt.runOnce
import ca.warp7.frc.ControllerState.*
import ca.warp7.frc.set
import ca.warp7.frc.withDriver
import ca.warp7.frc.withOperator
import ca.warp7.frc2019.constants.*
import ca.warp7.frc2019.subsystems.*
import ca.warp7.frc2019.subsystems.drive.DriveState
import ca.warp7.frc2019.subsystems.lift.LiftMotionPlanner
import ca.warp7.frc2019.subsystems.lift.LiftState
import ca.warp7.frc2019.subsystems.superstructure.SuperstructureState
import kotlin.math.absoluteValue

object MainLoop : Action {
    var pTrigger = false
    override fun start() {
        println("Robot State: Teleop")
        Drive.set(DriveState.kNeutralOutput)
        Limelight.set { isDriver = true }
        Outtake.set {
            speed = 0.0
            grabbing = true
            pushing = false
        }
    }

    override val shouldFinish: Boolean = false

    private var isStopOverrideOuttake = false

    override fun update() {
        isStopOverrideOuttake = false
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
            when (rightBumper) {
                Pressed -> Limelight.isDriver = false
                Released -> Limelight.isDriver = true
                else -> Unit
            }
            when {
                leftTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = -leftTriggerAxis
                    Intake.set { speed = -leftTriggerAxis * SuperstructureConstants.kIntakeSpeedScale }
                    isStopOverrideOuttake = false
                }
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = rightTriggerAxis
                    Intake.set { speed = rightTriggerAxis * SuperstructureConstants.kIntakeSpeedScale }
                    isStopOverrideOuttake = true
                }
                else -> Intake.set { speed = 0.0 }
            }
            if (aButton == Pressed) {
                Outtake.apply {
                    grabbing = false
                    if (pushing) {
                        pushing = true
                    } else {
                        set(queue {
                            +action { finishWhen { elapsed > 0.2 } }
                            +runOnce { pushing = true }
                        })
                    }
                }
            }
            when (Pressed) {
                xButton -> Outtake.set {
                    grabbing = !grabbing
                    pushing = false
                }
                else -> Unit
            }
            isFastOuttake = bButton == Pressed
        }
        withOperator {
            val trigger = leftTriggerAxis > ControlConstants.kControlDeadband

            if (leftYAxis.absoluteValue > ControlConstants.kLiftControlDeadband) {
                Lift.set(LiftState.kOpenLoop) { speed = leftYAxis }
            } else {
                LiftState.kOpenLoop.speed = 0.0
            }

            when (Pressed) {
                rightBumper -> LiftMotionPlanner.increaseSetpoint()
                leftBumper -> LiftMotionPlanner.decreaseSetpoint()
                else -> Unit
            }

            when (Pressed) {
                yButton -> {
                    LiftMotionPlanner.setpointType = HatchCargo.Hatch
                    Lift.set(LiftState.kGoToSetpoint) { setpoint = LiftMotionPlanner.getCoolSetpoint() }
                }
                bButton -> {
                    LiftMotionPlanner.setpointType = HatchCargo.Cargo
                    Lift.set(LiftState.kGoToSetpoint) { setpoint = LiftMotionPlanner.getCoolSetpoint() }
                }
                xButton -> Lift.set(LiftState.kGoToSetpoint) { setpoint = FieldConstants.kHatch1Height - LiftConstants.kHatchIntakeHeight }
                aButton -> Lift.set(LiftState.kGoToSetpoint) { setpoint = LiftConstants.kHomeHeightInches }
                else -> Unit
            }

            if (pTrigger != trigger) {
                Lift.set(LiftState.kGoToSetpoint) { setpoint = FieldConstants.kCargo2Height - 12.0 }
            }
            pTrigger = trigger
        }
        Superstructure.set(SuperstructureState.kPassThrough) {
            speed = passThroughSpeed
            openOuttake = isOpenOuttake
            fastOuttake = isFastOuttake
            stopOverrideOuttake = isStopOverrideOuttake
        }
    }
}