package ca.warp7.frc2019.constants

import ca.warp7.frc.PID
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration


object DriveConstants {

    /*
    ==================
    Physical constants
    ==================
    */

    private const val kWheelDiameter = 5.9//FIXME // Inches
    const val kWheelRadius = kWheelDiameter / 2.0
    const val kWheelCircumference = kWheelDiameter * Math.PI // Inches

    // Distance between left and right wheels in inches
    const val kTurningDiameter = 24.75 // FIXME This is measured for traction wheels not Colsons
    // The circumference the wheel base turns across in inches
    const val kTurningCircumference = kTurningDiameter * Math.PI

    private const val kScrubFactor = 1.45
    const val kEffectiveWheelBaseRadius = kTurningDiameter / 2 * kScrubFactor

    /*
    ======================
    Non-physical constants
    ======================
     */

    const val kLeftMaster = ElectricalConstants.kDriveLeftMasterTalonId
    const val kLeftFollowerA = ElectricalConstants.kDriveLeftFollowerAVictorId
    const val kLeftFollowerB = ElectricalConstants.kDriveLeftFollowerBVictorId
    const val kRightMaster = ElectricalConstants.kDriveRightMasterTalonId
    const val kRightFollowerA = ElectricalConstants.kDriveRightFollowerAVictorId
    const val kRightFollowerB = ElectricalConstants.kDriveRightFollowerBVictorId

    const val kDifferentialDeadband = 0.2
    const val kQuickTurnMultiplier = 0.7

    const val kTicksPerRevolution = 1024
    const val kTicksPerInch = kTicksPerRevolution / kWheelCircumference
    const val kTicksPerFootPer100ms = 12 * kTicksPerInch * 0.1
    const val kTicksPerMeterPer100ms = kTicksPerInch / 0.0254 * 0.1

    const val kMaxVelocity = 12.0 // ft/s
    const val kMaxAcceleration = 9.0 //  ft/s^2
    const val kMaxFreeSpeedVelocity = 14.38 // ft/s

    const val kMaxDx = 0.0254 // m
    const val kOpenLoopRamp = 0.15

    const val kMaxVolts = 12.0
    const val kSpeedPerVolt = kMaxFreeSpeedVelocity / kMaxVolts
    const val kTorquePerVolt = 0.0
    const val kFrictionVoltage = 0.0
    const val kLinearInertia = 60.0
    const val kAngularInertia = 10.0

    val kStraightPID: PID = PID(
            kP = 3.0, kI = 0.00001, kD = 16.0, kF = 0.0,
            errorEpsilon = 0.07, dErrorEpsilon = 0.04, minTimeInEpsilon = 0.3,
            maxOutput = DriveConstants.kMaxVelocity * 2
    )//meters

    val kTurnPID = PID(
            kP = 0.5, kI = 0.0001, kD = 1.5, kF = 0.0,
            errorEpsilon = 2.0, dErrorEpsilon = 1.0, minTimeInEpsilon = 0.5,
            maxOutput = DriveConstants.kMaxVelocity * 2
    )//degrees

    val kVelocityFeedforwardPID = PID(kP = 0.8, kI = 0.0, kD = 5.0, kF = 1.0)

    val kMasterTalonConfig = TalonSRXConfiguration().apply {

        slot0.apply {
            kP = kVelocityFeedforwardPID.kP
            kI = kVelocityFeedforwardPID.kI
            kD = kVelocityFeedforwardPID.kD
            kF = kVelocityFeedforwardPID.kF
        }

        openloopRamp = kOpenLoopRamp
        closedloopRamp = 0.0

        voltageCompSaturation = kMaxVolts // Max voltage

        // TalonSRXConfiguration

        primaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder
    }
}