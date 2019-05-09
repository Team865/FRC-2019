package ca.warp7.frc2019.io

import ca.warp7.frc.CSVLogger
import ca.warp7.frc.PID
import ca.warp7.frc.control.ControlInput
import ca.warp7.frc.control.RobotController
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc2019.constants.LimelightMode
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.Timer

class SimulatedIO : BaseIO {


    var enabled = false

    private val controlInput = ControlInput()

    override val driverInput: RobotController = controlInput.driver
    override val operatorInput: RobotController = controlInput.operator

    private val networkTableInstance: NetworkTableInstance = NetworkTableInstance.getDefault()
    private val telemetry: NetworkTable = networkTableInstance.getTable("RobotIO")

    override fun readInputs() {
        val newTime = Timer.getFPGATimestamp()
        dt = newTime - time
        time = newTime
        if (controlInput.driverIsXbox) {
            controlInput.updateDriver()
        }
        if (controlInput.driverIsXbox) {
            controlInput.updateOperator()
        }
    }

    override fun writeOutputs() {
        writeTelemetry()
//        if (enabled)
//            println(arrayOf(liftDemand, liftFeedforward).joinToString("\t") { it.f })
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
    override var visionErrorY: Double = 0.0
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

    private fun writeTelemetry() {
        telemetry.apply {
            getEntry("enableDriveEncoderInput").setBoolean(config.enableDriveEncoderInput)
            getEntry("enableLiftEncoderInput").setBoolean(config.enableLiftEncoderInput)
            getEntry("enableGyroInput").setBoolean(config.enableGyroInput)
            getEntry("enableLimelightInput").setBoolean(config.enableLimelightInput)
            if (config.enableDriveEncoderInput) {
                getEntry("leftPosition").setNumber(leftPosition)
                getEntry("rightPosition").setNumber(rightPosition)
                getEntry("leftVelocity").setNumber(leftVelocity)
                getEntry("rightVelocity").setNumber(rightVelocity)
            }
            if (config.enableLiftEncoderInput) {
                getEntry("liftPosition").setNumber(liftPosition)
                getEntry("liftVelocity").setNumber(liftVelocity)
            }
            if (config.enableLimelightInput) {
                getEntry("limelightConnected").setBoolean(limelightConnected)
                getEntry("foundVisionTarget").setBoolean(foundVisionTarget)
                getEntry("visionErrorX").setNumber(visionErrorX)
                getEntry("visionArea").setNumber(visionArea)
            }
            if (config.enableGyroInput) {
                getEntry("gyroConnected").setBoolean(gyroConnected)
                getEntry("fusedHeading").setNumber(fusedHeading)
                getEntry("angularVelocity").setNumber(angularVelocity)
            }
            getEntry("driveControlMode").setString(driveControlMode.name)
            getEntry("leftDemand").setNumber(leftDemand)
            getEntry("rightDemand").setNumber(rightDemand)
            getEntry("leftFeedforward").setNumber(leftFeedforward)
            getEntry("rightFeedforward").setNumber(rightFeedforward)

            getEntry("liftControlMode").setString(liftControlMode.name)
            getEntry("liftDemand").setNumber(liftDemand)
            getEntry("liftFeedforward").setNumber(liftFeedforward)

            getEntry("intakeSpeed").setNumber(intakeSpeed)
            getEntry("conveyorSpeed").setNumber(conveyorSpeed)
            getEntry("outtakeSpeed").setNumber(outtakeSpeed)

            getEntry("pushing").setBoolean(pushing)
            getEntry("grabbing").setBoolean(grabbing)
        }
    }

    override fun enable() {
        enabled = true
        time = Timer.getFPGATimestamp()
        controlInput.updateState()
    }

    override fun disable() {
        enabled = false
    }
}