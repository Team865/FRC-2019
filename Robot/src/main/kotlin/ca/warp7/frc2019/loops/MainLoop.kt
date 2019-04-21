package ca.warp7.frc2019.loops

import ca.warp7.actionkt.Action
import ca.warp7.actionkt.ActionControl
import ca.warp7.actionkt.runAfter
import ca.warp7.frc.LatchedBoolean
import ca.warp7.frc.PID
import ca.warp7.frc.applyDeadband
import ca.warp7.frc.control.ControllerState.*
import ca.warp7.frc2019.Looper
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.HatchCargo
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.constants.LimelightMode
import ca.warp7.frc2019.v2.subsystems.Drive
import ca.warp7.frc2019.v2.subsystems.Lift
import kotlin.math.abs
import kotlin.math.withSign

class MainLoop : Action {

    private val io: RobotIO = RobotIO

    val timerControl = ActionControl()

    val visionPID = PID(kP = 0.2, kI = 0.06, kD = 0.0, maxOutput = 0.4)
    val visionLog = RobotIO.getLogger("vision")
    val visionLatch = LatchedBoolean()
    val liftTriggerLatch = LatchedBoolean()

    override fun start() {
        RobotIO.enableTelemetry = true
        RobotIO.readingDriveEncoders = false
        RobotIO.readingLiftEncoder = true
        RobotIO.readingGyro = false
        RobotIO.readingLimelight = true
        RobotIO.setDriveRampRate(0.15)
        visionLog.writeHeaders("speedLimit", "correction",
                "left", "right", "visionErrorX", "visionArea")
    }

    override val shouldFinish: Boolean
        get() = false

    override fun stop() {
        visionLog.close()
    }

    override fun update() {
        RobotIO.driver.apply {
            val speed = applyDeadband(-leftYAxis, 1.0, 0.2)
            val curvature = applyDeadband(rightXAxis, 1.0, 0.1)
            val quickTurn = leftBumper == HeldDown
            if (quickTurn) {
                val turn = (curvature * curvature).withSign(curvature)
                Drive.setPercent(-turn, turn)
            } else {
                val turn = speed * curvature
                Drive.setNormalize(speed - turn, speed + turn)
            }
            when (rightBumper) {
                Pressed -> RobotIO.limelightMode = LimelightMode.Vision
                Released -> RobotIO.limelightMode = LimelightMode.Driver
                else -> Unit
            }
            val wantAligning = rightBumper == HeldDown
            val isAligning = wantAligning && RobotIO.foundVisionTarget && speed >= 0
                    && !quickTurn && abs(RobotIO.visionErrorX) < 15
            if (visionLatch.update(isAligning)) {
                visionLog.writeData(0, 0, 0, 0)
            }
            if (isAligning) {
                val speedLimit = 0.8 - 0.5 * RobotIO.visionArea
                RobotIO.leftDemand = RobotIO.leftDemand.coerceAtMost(speedLimit)
                RobotIO.rightDemand = RobotIO.rightDemand.coerceAtMost(speedLimit)
                if (speed == 0.0) {
                    RobotIO.leftDemand += Drive.model.frictionVoltage / 12.0
                    RobotIO.rightDemand += Drive.model.frictionVoltage / 12.0
                }
                val correction = visionPID.updateByError(Math.toRadians(-RobotIO.visionErrorX), RobotIO.dt)
                if (correction > 0) RobotIO.rightDemand += correction
                else if (correction < 0) RobotIO.leftDemand -= correction
                visionLog.writeData(speedLimit, correction, RobotIO.leftDemand,
                        RobotIO.rightDemand, RobotIO.visionErrorX, RobotIO.visionArea)
            }
            when {
                leftTriggerAxis > 0.2 -> {
                    RobotIO.outtakeSpeed = -leftTriggerAxis * 0.8
                    RobotIO.conveyorSpeed = -leftTriggerAxis * 0.9
                    RobotIO.intakeSpeed = -leftTriggerAxis
                }
                rightTriggerAxis > 0.2 -> {
                    RobotIO.outtakeSpeed = rightTriggerAxis * 0.8
                    RobotIO.conveyorSpeed = rightTriggerAxis * 0.9
                    RobotIO.intakeSpeed = rightTriggerAxis
                }
                else -> RobotIO.intakeSpeed = 0.0
            }
            if (aButton == Pressed) {
                if (!RobotIO.pushing) {
                    RobotIO.grabbing = false
                    timerControl.setAction(runAfter(0.3) {
                        RobotIO.invertPushing()
                    })
                    Looper.add(timerControl)
                } else RobotIO.pushing = !RobotIO.pushing
            }
            if (Pressed == xButton) {
                RobotIO.invertGrabbing()
                RobotIO.pushing = false
            }
            if (bButton == Pressed) RobotIO.outtakeSpeed = 1.0
        }

        RobotIO.operator.apply {
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
}