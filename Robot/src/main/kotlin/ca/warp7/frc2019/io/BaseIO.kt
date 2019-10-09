package ca.warp7.frc2019.io

import ca.warp7.frc.control.PID
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.input.RobotController
import ca.warp7.frc.log.CSVLogger
import ca.warp7.frc2019.constants.LimelightMode
import com.ctre.phoenix.motorcontrol.ControlMode

interface BaseIO {

    // Inputs

    val driverInput: RobotController
    val operatorInput: RobotController

    val time: Double // s
    val dt: Double // s

    val leftPosition: Double // rad
    val rightPosition: Double // rad
    val leftVelocity: Double // rad/s
    val rightVelocity: Double // rad/s

    val liftPosition: Int // ticks
    val liftVelocity: Int // ticks/100ms
    val hallEffectTriggered: Boolean

    val limelightConnected: Boolean
    val foundVisionTarget: Boolean
    val visionErrorX: Double // deg
    val visionErrorY: Double // deg
    val visionArea: Double // % of full image

    val gyroConnected: Boolean
    val fusedHeading: Double // rad
    val previousYaw: Rotation2D
    val yaw: Rotation2D
    val angularVelocity: Double // rad/s

    // Outputs

    var driveControlMode: ControlMode
    var leftDemand: Double
    var rightDemand: Double
    var leftFeedforward: Double // [-1, 1]
    var rightFeedforward: Double // [-1, 1]

    var liftControlMode: ControlMode
    var liftDemand: Double
    var liftFeedforward: Double

    var intakeSpeed: Double
    var conveyorSpeed: Double
    var outtakeSpeed: Double

    var pushing: Boolean
    var grabbing: Boolean

    fun invertGrabbing()
    fun invertPushing()

    // Configurations

    val config: IOConfig

    var drivePID: PID
    var driveRampRate: Double
    fun resetDrivePosition(positionRadians: Double)

    var liftPID: PID
    var liftRampRate: Double
    fun resetLiftPosition(positionRadians: Double)

    var limelightMode: LimelightMode

    // General Functions

    fun initialize()
    fun enable()
    fun disable()
    fun readInputs()
    fun writeOutputs()

    fun getLogger(name: String): CSVLogger
}