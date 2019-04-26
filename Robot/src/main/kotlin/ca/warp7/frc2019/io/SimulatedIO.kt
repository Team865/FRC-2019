package ca.warp7.frc2019.io

import ca.warp7.frc.CSVLogger
import ca.warp7.frc.PID
import ca.warp7.frc.control.ControlInput
import ca.warp7.frc.control.RobotController
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc2019.constants.LimelightMode
import com.ctre.phoenix.motorcontrol.ControlMode

class SimulatedIO : BaseIO {

    private val input = ControlInput()

    override val driverInput: RobotController = input.driver
    override val operatorInput: RobotController = input.operator

    override fun readInputs() {
    }

    override fun writeOutputs() {
    }

    override var time: Double = 0.0
    override var dt: Double = 0.0

    override var leftPosition: Double = 0.0
    override var rightPosition: Double = 0.0
    override var leftVelocity: Double = 0.0
    override var rightVelocity: Double = 0.0

    override var liftPosition: Int = 0
    override var liftVelocity: Int = 0
    override var hallEffectTriggered: Boolean = false

    override var limelightConnected: Boolean = false
    override var foundVisionTarget: Boolean = false
    override var visionErrorX: Double = 0.0
    override var visionArea: Double = 0.0

    override var gyroConnected: Boolean = false
    override var fusedHeading: Double = 0.0
    override var previousYaw: Rotation2D = Rotation2D.identity
    override var yaw: Rotation2D = Rotation2D.identity
    override var angularVelocity: Double = 0.0

    override var driveControlMode: ControlMode = ControlMode.PercentOutput
    override var leftDemand: Double = 0.0
    override var rightDemand: Double = 0.0
    override var leftFeedforward: Double = 0.0
    override var rightFeedforward: Double = 0.0

    override var liftControlMode: ControlMode = ControlMode.PercentOutput
    override var liftDemand: Double = 0.0
    override var liftFeedforward: Double = 0.0

    override var intakeSpeed: Double = 0.0
    override var conveyorSpeed: Double = 0.0
    override var outtakeSpeed: Double = 0.0

    override var pushing: Boolean = false
    override var grabbing: Boolean = false

    override fun invertGrabbing() {
        grabbing = !grabbing
    }

    override fun invertPushing() {
        pushing = !pushing
    }

    override val config = IOConfig()
    override var drivePID = PID()
    override var driveRampRate: Double = 0.0
    override fun resetDrivePosition(positionRadians: Double) {
    }

    override var liftPID: PID = PID()
    override var liftRampRate: Double = 0.0
    override fun resetLiftPosition(positionRadians: Double) {
    }

    override var limelightMode: LimelightMode = LimelightMode.Vision

    override fun getLogger(name: String): CSVLogger {
        return object : CSVLogger {
            override fun withHeaders(vararg headers: String): CSVLogger {
                return this
            }

            override fun writeData(vararg data: Number) {
            }

            override fun close() {
            }

        }
    }

    override fun initialize() {
    }

    override fun enable() {
    }

    override fun disable() {
    }
}