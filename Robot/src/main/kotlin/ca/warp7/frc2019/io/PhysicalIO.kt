package ca.warp7.frc2019.io

import ca.warp7.frc.CSVLogManager
import ca.warp7.frc.CSVLogger
import ca.warp7.frc.PID
import ca.warp7.frc.control.*
import ca.warp7.frc.control.RobotController
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.fromRadians
import ca.warp7.frc.geometry.radians
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.constants.IOConstants
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.constants.LimelightMode
import com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput
import com.ctre.phoenix.motorcontrol.DemandType.ArbitraryFeedForward
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.hal.HAL
import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.*
import edu.wpi.first.wpilibj.livewindow.LiveWindow

class PhysicalIO : BaseIO {
    private val leftMaster: TalonSRX = talonSRX(IOConstants.kLeftMaster, DriveConstants.kMasterTalonConfig)
            .followedBy(VictorSPX(IOConstants.kLeftFollowerA), VictorSPX(IOConstants.kLeftFollowerB))
    private val rightMaster: TalonSRX = talonSRX(IOConstants.kRightMaster, DriveConstants.kMasterTalonConfig)
            .followedBy(VictorSPX(IOConstants.kRightFollowerA), VictorSPX(IOConstants.kRightFollowerB))

    private val intakeMaster: VictorSPX = victorSPX(IOConstants.kIntakeMaster, neutralMode = NeutralMode.Coast)
    private val conveyorLeftMaster: VictorSPX = victorSPX(IOConstants.kConveyorLeftMaster)
    private val conveyorRightMaster: VictorSPX = victorSPX(IOConstants.kConveyorRightMaster)
    private val outtakeLeftMaster: VictorSPX = victorSPX(IOConstants.kOuttakeLeftMaster)
    private val outtakeRightMaster: VictorSPX = victorSPX(IOConstants.kOuttakeRightMaster)
    private val pusher: Solenoid = lazySolenoid(IOConstants.kPusherSolenoid)
    private val grabber: Solenoid = lazySolenoid(IOConstants.kGrabberSolenoid)

    private val liftMaster: TalonSRX = talonSRX(IOConstants.kLiftMaster, LiftConstants.kMasterTalonConfig)
            .followedBy(victorSPX(IOConstants.kLiftFollower, inverted = true)).apply { setSensorPhase(true) }

    private val liftHallEffect = DigitalInput(IOConstants.kLiftHallEffect)
    private val ahrs = AHRS(SPI.Port.kMXP, 100)

    private val networkTableInstance: NetworkTableInstance = NetworkTableInstance.getDefault()
    private val limelight: NetworkTable = networkTableInstance.getTable("limelight")
    private val telemetry: NetworkTable = networkTableInstance.getTable("RobotIO")

    private val controlInput = ControlInput()
    private val csvLogManager = CSVLogManager()

    private var enableOutputs = false

    private val ds: DriverStation = DriverStation.getInstance()

    override val driverInput: RobotController = controlInput.driver
    override val operatorInput: RobotController = controlInput.operator

    override val config = IOConfig()

    override var time = 0.0 // s
    override var dt = 0.02 // s

    override var leftPosition = 0.0 // rad
    override var rightPosition = 0.0 // rad
    override var leftVelocity = 0.0 // rad/s
    override var rightVelocity = 0.0 // rad/s

    override var liftPosition = 0 // ticks
    override var liftVelocity = 0 // ticks/100ms
    override var hallEffectTriggered = false

    override var limelightConnected = false
    override var foundVisionTarget = false
    override var visionErrorX = 0.0 // deg
    override var visionErrorY = 0.0 // deg
    override var visionArea = 0.0 // % of full image

    override var gyroConnected = false
    override var fusedHeading = 0.0 // rad
    override var previousYaw = Rotation2D.identity
    override var yaw: Rotation2D = Rotation2D.identity
    override var angularVelocity = 0.0 // rad/s

    override fun readInputs() {
        val newTime = Timer.getFPGATimestamp()
        dt = newTime - time
        time = newTime
        if (config.enableDriverInput) {
            controlInput.updateDriver()
        }
        if (config.enableOperatorInput) {
            controlInput.updateOperator()
        }
        if (config.enableLiftEncoderInput) {
            liftPosition = liftMaster.selectedSensorPosition
            liftVelocity = liftMaster.selectedSensorVelocity
            hallEffectTriggered = !liftHallEffect.get()
        }
        if (config.enableDriveEncoderInput) {
            leftPosition = leftMaster.selectedSensorPosition / DriveConstants.kTicksPerRadian
            rightPosition = -rightMaster.selectedSensorPosition / DriveConstants.kTicksPerRadian
            leftVelocity = leftMaster.selectedSensorVelocity / DriveConstants.kTicksPerRadian * 10
            rightVelocity = -rightMaster.selectedSensorVelocity / DriveConstants.kTicksPerRadian * 10
        }
        if (config.enableLimelightInput) {
            if (!limelightConnected && limelight.getEntry("tv").exists()) limelightConnected = true
            if (limelightConnected) {
                foundVisionTarget = limelight.getEntry("tv").getDouble(0.0).toInt() == 1
                if (foundVisionTarget) {
                    visionErrorX = limelight.getEntry("tx").getDouble(0.0)
                    visionErrorY = limelight.getEntry("ty").getDouble(0.0)
                    visionArea = limelight.getEntry("ta").getDouble(0.0)
                }
            }
        }
        if (config.enableGyroInput) {
            if (!gyroConnected && ahrs.isConnected && !ahrs.isCalibrating) gyroConnected = true
            if (gyroConnected) {
                fusedHeading = Math.toRadians(ahrs.fusedHeading.toDouble())
                previousYaw = yaw
                yaw = Rotation2D.fromRadians(fusedHeading)
                angularVelocity = (previousYaw - yaw).radians / dt
            }
        }
    }

    override var driveControlMode = PercentOutput
    override var leftDemand = 0.0
    override var rightDemand = 0.0
    override var leftFeedforward = 0.0
    override var rightFeedforward = 0.0

    override var liftControlMode = PercentOutput
    override var liftDemand = 0.0
    override var liftFeedforward = 0.0

    override var intakeSpeed = 0.0
    override var conveyorSpeed = 0.0
    override var outtakeSpeed = 0.0

    override var pushing = false
    override var grabbing = true

    override fun writeOutputs() {
        if (enableOutputs) writeEnabledOutputs()
        if (config.enableTelemetryOutput) writeTelemetry()
    }

    private fun writeEnabledOutputs() {
        leftMaster.set(driveControlMode, leftDemand, ArbitraryFeedForward, leftFeedforward)
        rightMaster.set(driveControlMode, -rightDemand, ArbitraryFeedForward, -rightFeedforward)
        intakeMaster.set(PercentOutput, intakeSpeed)
        conveyorLeftMaster.set(PercentOutput, conveyorSpeed)
        conveyorRightMaster.set(PercentOutput, conveyorSpeed)
        outtakeLeftMaster.set(PercentOutput, outtakeSpeed)
        outtakeRightMaster.set(PercentOutput, -outtakeSpeed)
        pusher.set(pushing)
        grabber.set(grabbing)
        liftMaster.set(liftControlMode, liftDemand, ArbitraryFeedForward, liftFeedforward)
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

    override var drivePID: PID = DriveConstants.kVelocityFeedforwardPID
        set(value) {
            leftMaster.setPID(value)
            rightMaster.setPID(value)
            field = value
        }

    override var driveRampRate: Double = 0.0
        set(value) {
            leftMaster.configOpenloopRamp(value, 0)
            rightMaster.configOpenloopRamp(value, 0)
            field = value
        }

    override fun resetDrivePosition(positionRadians: Double) {
        val positionTicks = (positionRadians * DriveConstants.kTicksPerRadian).toInt()
        leftMaster.selectedSensorPosition = positionTicks
        rightMaster.selectedSensorPosition = -positionTicks
    }

    override var liftPID: PID = PID()
        set(value) {
            liftMaster.setPID(value)
            field = value
        }

    override fun resetLiftPosition(positionRadians: Double) {
        val positionTicks = (positionRadians * LiftConstants.kTicksPerRadian).toInt()
        liftMaster.selectedSensorPosition = positionTicks
    }

    override var liftRampRate: Double = 0.0
        set(value) {
            liftMaster.configOpenloopRamp(value, 0)
            field = value
        }

    override var limelightMode = LimelightMode.Driver
        set(value) {
            limelight.getEntry("camMode").setDouble(value.value)
            limelight.getEntry("ledMode").setDouble(value.value)
            field = value
        }

    override fun invertGrabbing() {
        grabbing = !grabbing
    }

    override fun invertPushing() {
        pushing = !pushing
    }

    override fun getLogger(name: String): CSVLogger = csvLogManager.getLogger(name)

    override fun initialize() {
        time = Timer.getFPGATimestamp()
        LiveWindow.disableAllTelemetry()
        csvLogManager.startSession(if (ds.isFMSAttached) {
            val allianceStation = HAL.getAllianceStation()?.toString() ?: "None"
            val matchNumber = ds.matchNumber
            val type = ds.matchType
            "${type}_${matchNumber}_$allianceStation"
        } else "Test")
    }

    override fun enable() {
        enableOutputs = true
        ahrs.zeroYaw()
        resetDrivePosition(0.0)
        resetLiftPosition(0.0)
        limelightMode = LimelightMode.Driver
    }

    override fun disable() {
        enableOutputs = false

        leftMaster.neutralOutput()
        rightMaster.neutralOutput()

        intakeMaster.neutralOutput()
        conveyorLeftMaster.neutralOutput()
        conveyorRightMaster.neutralOutput()
        outtakeLeftMaster.neutralOutput()
        outtakeRightMaster.neutralOutput()
        pusher.set(false)
        grabber.set(true)

        liftMaster.neutralOutput()

        driveControlMode = PercentOutput
        leftDemand = 0.0
        rightDemand = 0.0
        leftFeedforward = 0.0
        rightFeedforward = 0.0

        intakeSpeed = 0.0
        conveyorSpeed = 0.0
        outtakeSpeed = 0.0
        pushing = false
        grabbing = true

        liftControlMode = PercentOutput
        liftDemand = 0.0
        liftFeedforward = 0.0

        csvLogManager.closeLoggers()

        config.apply {
            enableDriverInput = false
            enableOperatorInput = false
        }
    }
}