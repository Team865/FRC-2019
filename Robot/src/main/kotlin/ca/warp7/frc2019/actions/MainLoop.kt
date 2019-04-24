package ca.warp7.frc2019.actions

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
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Lift
import kotlin.math.abs
import kotlin.math.withSign

class MainLoop : Action {

    private val io: RobotIO = RobotIO

    val timerControl = ActionControl()

    val visionPID = PID(kP = 0.2, kI = 0.06, kD = 0.0, maxOutput = 0.4)
    val visionLog = io.getLogger("vision")
            .withHeaders("speedLimit", "correction",
                    "left", "right", "visionErrorX", "visionArea")
    val visionLatch = LatchedBoolean()
    val liftTriggerLatch = LatchedBoolean()

    override fun start() {
        io.enableTelemetry = true
        io.readingDriveEncoders = false
        io.readingLiftEncoder = true
        io.readingGyro = false
        io.readingLimelight = true
        io.driveRampRate = 0.15
    }

    override val shouldFinish: Boolean
        get() = false

    override fun update() {
        io.driver.apply {
            val xSpeed = applyDeadband(-leftYAxis, 1.0, 0.2)
            val zRotation = applyDeadband(rightXAxis, 1.0, 0.1)
            val isQuickTurn = leftBumper == HeldDown
            updateCurvatureDrive(xSpeed, zRotation, isQuickTurn)

            when (rightBumper) {
                Pressed -> io.limelightMode = LimelightMode.Vision
                Released -> io.limelightMode = LimelightMode.Driver
                else -> Unit
            }
            val wantAligning = rightBumper == HeldDown && !isQuickTurn
            updateVisionAlignment(wantAligning, xSpeed)

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

    fun updatePassthrough(speed: Double) {
        io.outtakeSpeed = speed * 0.8
        io.conveyorSpeed = speed * 0.9
        io.intakeSpeed = speed
    }

    fun updateVisionAlignment(wantAligning: Boolean, xSpeed: Double) {
        val isAligning = wantAligning && io.foundVisionTarget && xSpeed >= 0 && abs(io.visionErrorX) < 15
        if (visionLatch.update(isAligning)) {
            visionLog.writeData(0, 0, 0, 0)
        }
        if (isAligning) {
            val speedLimit = 0.8 - 0.5 * io.visionArea
            io.leftDemand = io.leftDemand.coerceAtMost(speedLimit)
            io.rightDemand = io.rightDemand.coerceAtMost(speedLimit)
            if (xSpeed == 0.0) {
                io.leftDemand += Drive.model.frictionVoltage / 12.0
                io.rightDemand += Drive.model.frictionVoltage / 12.0
            }
            val correction = visionPID.updateByError(Math.toRadians(-io.visionErrorX), io.dt)
            if (correction > 0) io.rightDemand += correction
            else if (correction < 0) io.leftDemand += correction
            visionLog.writeData(speedLimit, correction, io.leftDemand,
                    io.rightDemand, io.visionErrorX, io.visionArea)
        }
    }

    private var quickStopAccumulator = 0.0

    /**
     * Curvature drive method for differential drive platform.
     *
     *
     * The rotation argument controls the curvature of the robot's path rather than its rate of
     * heading change. This makes the robot more controllable at high speeds. Also handles the
     * robot's quick turn functionality - "quick turn" overrides constant-curvature turning for
     * turn-in-place maneuvers.
     *
     * @param xSpeed      The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
     * @param zRotation   The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is
     * positive.
     * @param isQuickTurn If set, overrides constant-curvature turning for
     * turn-in-place maneuvers.
     */
    fun updateCurvatureDrive(xSpeed: Double, zRotation: Double, isQuickTurn: Boolean) {

        val angularPower: Double

        if (isQuickTurn) {
            if (Math.abs(xSpeed) < 0.2) {
                val alpha = 0.1
                quickStopAccumulator = (1 - alpha) * quickStopAccumulator + alpha * zRotation * 2.0
            }
            angularPower = (zRotation * zRotation).withSign(zRotation).coerceIn(-0.8, 0.8)
        } else {
            angularPower = Math.abs(xSpeed) * zRotation - quickStopAccumulator
            when {
                quickStopAccumulator > 1 -> quickStopAccumulator -= 1.0
                quickStopAccumulator < -1 -> quickStopAccumulator += 1.0
                else -> quickStopAccumulator = 0.0
            }
        }

        var leftMotorOutput = xSpeed + angularPower
        var rightMotorOutput = xSpeed - angularPower

        // If rotation is overpowered, reduce both outputs to within acceptable range
        if (isQuickTurn) {
            when {
                leftMotorOutput > 1.0 -> {
                    rightMotorOutput -= leftMotorOutput - 1.0
                    leftMotorOutput = 1.0
                }
                rightMotorOutput > 1.0 -> {
                    leftMotorOutput -= rightMotorOutput - 1.0
                    rightMotorOutput = 1.0
                }
                leftMotorOutput < -1.0 -> {
                    rightMotorOutput -= leftMotorOutput + 1.0
                    leftMotorOutput = -1.0
                }
                rightMotorOutput < -1.0 -> {
                    leftMotorOutput -= rightMotorOutput + 1.0
                    rightMotorOutput = -1.0
                }
            }
        }

        // Normalize the wheel speeds
        val maxMagnitude = Math.max(Math.abs(leftMotorOutput), Math.abs(rightMotorOutput))
        if (maxMagnitude > 1.0) {
            leftMotorOutput /= maxMagnitude
            rightMotorOutput /= maxMagnitude
        }

        Drive.setPercent(leftMotorOutput, rightMotorOutput)
    }
}