package ca.warp7.frc2019.actions

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.ActionControl
import ca.warp7.actionkt.runAfter
import ca.warp7.frc.LatchedBoolean
import ca.warp7.frc.applyDeadband
import ca.warp7.frc.control.ControllerState.*
import ca.warp7.frc2019.Looper
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.HatchCargo
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.constants.LimelightMode
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.Limelight
import kotlin.math.abs

class MainLoop : Action {

    private val io: BaseIO = ioInstance()

    val timerControl = ActionControl()
    val liftTriggerLatch = LatchedBoolean()

    override fun start() {
        io.config.apply {
            enableTelemetryOutput = true
            enableDriveEncoderInput = false
            enableLiftEncoderInput = true
            enableGyroInput = false
            enableLimelightInput = true
            enableDriverInput = true
            enableOperatorInput = true
        }
        Lift.feedforwardEnabled = false
        io.driveRampRate = 0.15
    }

    override val shouldFinish: Boolean
        get() = false

    override fun update() {
        io.driverInput.apply {

            val xSpeed = applyDeadband(-leftYAxis, 1.0, 0.2)
            val zRotation = applyDeadband(rightXAxis, 1.0, 0.2)
            val isQuickTurn = leftBumper == HeldDown
            Drive.updateCurvatureDrive(xSpeed, zRotation, isQuickTurn)

            when (rightBumper) {
                Pressed -> io.limelightMode = LimelightMode.Vision
                Released -> io.limelightMode = LimelightMode.Driver
                else -> Unit
            }
            val wantAligning = rightBumper == HeldDown && !isQuickTurn
            Limelight.updateDriveAlignment(wantAligning, xSpeed)

            when {
                leftTriggerAxis > 0.2 -> updatePassthrough(-leftTriggerAxis)
                rightTriggerAxis > 0.2 -> updatePassthrough(rightTriggerAxis)
                else -> io.intakeSpeed = 0.0
            }
            if (bButton == Pressed) io.outtakeSpeed = 1.0

            when {
                aButton == Pressed -> if (!io.pushing) {
                    io.grabbing = false
                    timerControl.setAction(runAfter(0.3) { io.invertPushing() })
                    Looper.add(timerControl)
                } else io.invertPushing()

                xButton == Pressed -> {
                    io.invertGrabbing()
                    io.pushing = false
                }
            }
        }

        io.operatorInput.apply {

            if (abs(leftYAxis) > 0.2) {
                Lift.manualSpeed = applyDeadband(leftYAxis, 1.0, 0.2)
                Lift.isManual = true
            } else Lift.manualSpeed = 0.0

            when (Pressed) {
                rightBumper -> Lift.increaseSetpoint()
                leftBumper -> Lift.decreaseSetpoint()
                yButton -> {
                    Lift.setpointType = HatchCargo.Hatch
                    Lift.setpointInches = Lift.getCoolSetpoint()
                    Lift.isManual = false
                }
                bButton -> {
                    Lift.setpointType = HatchCargo.Cargo
                    Lift.setpointInches = Lift.getCoolSetpoint()
                    Lift.isManual = false
                }
                xButton -> {
                    Lift.setpointInches = FieldConstants.kHatch1Height - LiftConstants.kHatchIntakeHeight
                    Lift.isManual = false
                }
                aButton -> {
                    Lift.setpointInches = LiftConstants.kHomeHeightInches
                    Lift.isManual = false
                }
                startButton -> {
                    Lift.feedforwardEnabled = !Lift.feedforwardEnabled
                }
                else -> Unit
            }

            if (liftTriggerLatch.update(leftTriggerAxis > 0.2)) {
                Lift.setpointInches = FieldConstants.kCargo2Height - 12.0
                Lift.isManual = false
            }

            if (Lift.isManual) Lift.updateManualControl()
            else Lift.updatePositionControl()
        }
    }

    fun updatePassthrough(speed: Double) {
        io.outtakeSpeed = speed * 0.8
        io.conveyorSpeed = speed * 0.9
        io.intakeSpeed = speed
    }
}