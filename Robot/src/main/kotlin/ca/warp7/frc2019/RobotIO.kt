package ca.warp7.frc2019

import ca.warp7.frc.CSVLogManager
import ca.warp7.frc.CSVLogger
import ca.warp7.frc.PID
import ca.warp7.frc.control.*
import ca.warp7.frc.control.RobotController
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.fromRadians
import ca.warp7.frc.geometry.radians
import ca.warp7.frc2019.constants.*
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

object RobotIO {
    private val leftMaster: TalonSRX = talonSRX(DriveConstants.kLeftMaster, DriveConstants.kMasterTalonConfig)
            .followedBy(VictorSPX(DriveConstants.kLeftFollowerA), VictorSPX(DriveConstants.kLeftFollowerB))
    private val rightMaster: TalonSRX = talonSRX(DriveConstants.kRightMaster, DriveConstants.kMasterTalonConfig)
            .followedBy(VictorSPX(DriveConstants.kRightFollowerA), VictorSPX(DriveConstants.kRightFollowerB))

    private val intakeMaster: VictorSPX = victorSPX(IntakeConstants.kMaster, neutralMode = NeutralMode.Coast)
    private val conveyorLeftMaster: VictorSPX = victorSPX(ConveyorConstants.kLeftMaster)
    private val conveyorRightMaster: VictorSPX = victorSPX(ConveyorConstants.kRightMaster)
    private val outtakeLeftMaster: VictorSPX = victorSPX(OuttakeConstants.kLeftMaster)
    private val outtakeRightMaster: VictorSPX = victorSPX(OuttakeConstants.kRightMaster)
    private val pusher: Solenoid = lazySolenoid(HatchConstants.kPusherSolenoid)
    private val grabber: Solenoid = lazySolenoid(HatchConstants.kGrabberSolenoid)

    private val liftMaster: TalonSRX = talonSRX(LiftConstants.kMaster, LiftConstants.kMasterTalonConfig)
            .followedBy(victorSPX(LiftConstants.kFollower, inverted = true)).apply { setSensorPhase(true) }

    private val liftHallEffect = DigitalInput(LiftConstants.kHallEffect)
    private val ahrs = AHRS(SPI.Port.kMXP)

    private val networkTableInstance: NetworkTableInstance = NetworkTableInstance.getDefault()
    private val limelight: NetworkTable = networkTableInstance.getTable("limelight")
    private val telemetry: NetworkTable = networkTableInstance.getTable("RobotIO")

    private val controlInput = ControlInput()
    private val csvLogManager = CSVLogManager()

    val ds: DriverStation = DriverStation.getInstance()
    val driver: RobotController = controlInput.driver
    val operator: RobotController = controlInput.operator

    var enableOutputs = false
    var enableTelemetry = true

    var readingDriverInput = true
    var readingOperatorInput = true
    var readingDriveEncoders = true
    var readingLiftEncoder = true
    var readingGyro = true
    var readingLimelight = true

    var time = 0.0 // s
    var dt = 0.02 // s

    var leftPosition = 0.0 // rad
    var rightPosition = 0.0 // rad
    var leftVelocity = 0.0 // rad/s
    var rightVelocity = 0.0 // rad/s

    var liftPosition = 0 // ticks
    var liftVelocity = 0 // ticks/100ms
    var hallEffectTriggered = false

    var limelightConnected = false
    var foundVisionTarget = false
    var visionErrorX = 0.0 // deg
    var visionArea = 0.0 // % of full image

    var gyroConnected = false
    var fusedHeading = 0.0 // rad
    var previousYaw = Rotation2D.identity
    var yaw: Rotation2D = Rotation2D.identity
    var angularVelocity = 0.0 // rad/s

    fun readInputs() {
        val newTime = Timer.getFPGATimestamp()
        dt = newTime - time
        time = newTime
        if (readingDriverInput) {
            controlInput.updateDriver()
        }
        if (readingOperatorInput) {
            controlInput.updateOperator()
        }
        if (readingLiftEncoder) {
            liftPosition = liftMaster.selectedSensorPosition
            liftVelocity = liftMaster.selectedSensorVelocity
            hallEffectTriggered = !liftHallEffect.get()
        }
        if (readingDriveEncoders) {
            leftPosition = leftMaster.selectedSensorPosition / DriveConstants.kTicksPerRadian
            rightPosition = -rightMaster.selectedSensorPosition / DriveConstants.kTicksPerRadian
            leftVelocity = leftMaster.selectedSensorVelocity / DriveConstants.kTicksPerRadian * 10
            rightVelocity = -rightMaster.selectedSensorVelocity / DriveConstants.kTicksPerRadian * 10
        }
        if (readingLimelight) {
            if (!limelightConnected && limelight.getEntry("tv").exists()) limelightConnected = true
            if (limelightConnected) {
                foundVisionTarget = limelight.getEntry("tv").getDouble(0.0).toInt() == 1
                if (foundVisionTarget) {
                    visionErrorX = limelight.getEntry("tv").getDouble(0.0)
                    visionArea = limelight.getEntry("ta").getDouble(0.0)
                }
            }
        }
        if (readingGyro) {
            if (!gyroConnected && ahrs.isConnected && !ahrs.isCalibrating) gyroConnected = true
            if (gyroConnected) {
                fusedHeading = Math.toRadians(ahrs.fusedHeading.toDouble())
                previousYaw = yaw
                yaw = Rotation2D.fromRadians(fusedHeading)
                angularVelocity = (previousYaw - yaw).radians / dt
            }
        }
    }

    var driveControlMode = PercentOutput
    var leftDemand = 0.0
    var rightDemand = 0.0
    var leftFeedforward = 0.0
    var rightFeedforward = 0.0

    var liftControlMode = PercentOutput
    var liftDemand = 0.0
    var liftFeedforward = 0.0

    var intakeSpeed = 0.0
    var conveyorSpeed = 0.0
    var outtakeSpeed = 0.0

    var pushing = false
    var grabbing = true

    fun writeOutputs() {
        if (enableOutputs) writeEnabledOutputs()
        if (enableTelemetry) writeTelemetry()
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
            getEntry("readingDriveEncoders").setBoolean(readingDriveEncoders)
            getEntry("readingLiftEncoder").setBoolean(readingLiftEncoder)
            getEntry("readingGyro").setBoolean(readingGyro)
            getEntry("readingLimelight").setBoolean(readingLimelight)
            if (readingDriveEncoders) {
                getEntry("leftPosition").setNumber(leftPosition)
                getEntry("rightPosition").setNumber(rightPosition)
                getEntry("leftVelocity").setNumber(leftVelocity)
                getEntry("rightVelocity").setNumber(rightVelocity)
            }
            if (readingLiftEncoder) {
                getEntry("liftPosition").setNumber(liftPosition)
                getEntry("liftVelocity").setNumber(liftVelocity)
            }
            if (readingLimelight) {
                getEntry("limelightConnected").setBoolean(limelightConnected)
                getEntry("foundVisionTarget").setBoolean(foundVisionTarget)
                getEntry("visionErrorX").setNumber(visionErrorX)
                getEntry("visionArea").setNumber(visionArea)
            }
            if (readingGyro) {
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

    var drivePID: PID = DriveConstants.kVelocityFeedforwardPID
        set(value) {
            leftMaster.setPID(value)
            rightMaster.setPID(value)
            field = value
        }

    fun resetDrivePosition(positionRadians: Double) {
        val positionTicks = (positionRadians * DriveConstants.kTicksPerRadian).toInt()
        leftMaster.selectedSensorPosition = positionTicks
        rightMaster.selectedSensorPosition = -positionTicks
    }

    fun setDriveRampRate(secondsFromNeutralToFull: Double) {
        leftMaster.configOpenloopRamp(secondsFromNeutralToFull, 0)
        rightMaster.configOpenloopRamp(secondsFromNeutralToFull, 0)
    }

    var liftPID: PID = PID()
        set(value) {
            liftMaster.setPID(value)
            field = value
        }

    fun resetLiftPosition(positionRadians: Double) {
        val positionTicks = (positionRadians * LiftConstants.kTicksPerRadian).toInt()
        liftMaster.selectedSensorPosition = positionTicks
    }

    fun setLiftRampRate(secondsFromNeutralToFull: Double) {
        liftMaster.configOpenloopRamp(secondsFromNeutralToFull, 0)
    }

    var limelightMode = LimelightMode.Driver
        set(value) {
            limelight.getEntry("camMode").setDouble(value.value)
            limelight.getEntry("ledMode").setDouble(value.value)
            field = value
        }

    fun invertGrabbing() {
        grabbing = !grabbing
    }

    fun invertPushing() {
        pushing = !pushing
    }

    fun getLogger(name: String): CSVLogger = csvLogManager.getLogger(name)

    fun initialize() {
        enableOutputs = false
        enableTelemetry = true
        readingDriveEncoders = true
        readingLiftEncoder = true
        readingGyro = true
        readingLimelight = true
        time = Timer.getFPGATimestamp()
        previousYaw = Rotation2D.identity
        LiveWindow.disableAllTelemetry()
    }

    fun enable() {
        enableOutputs = true
        ahrs.zeroYaw()
        resetDrivePosition(0.0)
        resetLiftPosition(0.0)
        limelightMode = LimelightMode.Driver
        csvLogManager.startSession(if (ds.isFMSAttached) {
            val allianceStation = HAL.getAllianceStation()?.toString() ?: "None"
            val matchNumber = ds.matchNumber
            val type = ds.matchType
            "${type}_${matchNumber}_$allianceStation"
        } else "Test")
    }

    fun disable() {
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

        csvLogManager.endSession()
    }
}