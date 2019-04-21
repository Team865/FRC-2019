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
    val visionLog = io.getLogger("vision")
    val visionLatch = LatchedBoolean()
    val liftTriggerLatch = LatchedBoolean()

    override fun start() {
        io.enableTelemetry = true
        io.readingDriveEncoders = false
        io.readingLiftEncoder = true
        io.readingGyro = false
        io.readingLimelight = true
        io.setDriveRampRate(0.15)
        visionLog.writeHeaders("speedLimit", "correction",
                "left", "right", "visionErrorX", "visionArea")
    }

    override val shouldFinish: Boolean
        get() = false

    override fun stop() {
        visionLog.close()
    }

    override fun update() {
        io.driver.apply {
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
                Pressed -> io.limelightMode = LimelightMode.Vision
                Released -> io.limelightMode = LimelightMode.Driver
                else -> Unit
            }
            val wantAligning = rightBumper == HeldDown
            val isAligning = wantAligning && io.foundVisionTarget && speed >= 0
                    && !quickTurn && abs(io.visionErrorX) < 15
            if (visionLatch.update(isAligning)) {
                visionLog.writeData(0, 0, 0, 0)
            }
            if (isAligning) {
                val speedLimit = 0.8 - 0.5 * io.visionArea
                io.leftDemand = io.leftDemand.coerceAtMost(speedLimit)
                io.rightDemand = io.rightDemand.coerceAtMost(speedLimit)
                if (speed == 0.0) {
                    io.leftDemand += Drive.model.frictionVoltage / 12.0
                    io.rightDemand += Drive.model.frictionVoltage / 12.0
                }
                val correction = visionPID.updateByError(Math.toRadians(-io.visionErrorX), io.dt)
                if (correction > 0) io.rightDemand += correction
                else if (correction < 0) io.leftDemand -= correction
                visionLog.writeData(speedLimit, correction, io.leftDemand,
                        io.rightDemand, io.visionErrorX, io.visionArea)
            }
            when {
                leftTriggerAxis > 0.2 -> {
                    io.outtakeSpeed = -leftTriggerAxis * 0.8
                    io.conveyorSpeed = -leftTriggerAxis * 0.9
                    io.intakeSpeed = -leftTriggerAxis
                }
                rightTriggerAxis > 0.2 -> {
                    io.outtakeSpeed = rightTriggerAxis * 0.8
                    io.conveyorSpeed = rightTriggerAxis * 0.9
                    io.intakeSpeed = rightTriggerAxis
                }
                else -> io.intakeSpeed = 0.0
            }
            if (aButton == Pressed) {
                if (!io.pushing) {
                    io.grabbing = false
                    timerControl.setAction(runAfter(0.3) {
                        io.invertPushing()
                    })
                    Looper.add(timerControl)
                } else io.pushing = !io.pushing
            }
            if (Pressed == xButton) {
                io.invertGrabbing()
                io.pushing = false
            }
            if (bButton == Pressed) io.outtakeSpeed = 1.0
        }

        io.operator.apply {
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