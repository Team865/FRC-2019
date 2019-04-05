package ca.warp7.frc2019.constants

import ca.warp7.frc2019.subsystems.drive.unused.PID
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

    private const val kScrubFactor = 1.4
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

    const val kSegmentLength = 0.0254 // m

    const val kVIntercept = 0.0 // FIXME

    private const val kOpenLoopRamp = 0.15

    const val kMaxVolts = 12.0
    const val kSpeedPerVolt = kMaxFreeSpeedVelocity / kMaxVolts
    const val kTorquePerVolt = 0.0
    const val kLinearInertia = 60.0
    const val kAngularInertia = 10.0

    val straightPID: PID = PID(
            kP = 6.0, kI = 0.0015, kD = 1.0, kF = 0.0,
            errorEpsilon = 0.25, dErrorEpsilon = 0.2, minTimeInEpsilon = 0.3,
            maxOutput = kMaxVelocity
    )//meters

    val kMasterTalonConfig = TalonSRXConfiguration().apply {

        // TODO Position PID slot
        slot0.apply {
            kP = 1.0
            kI = 0.01
            kD = 0.6
            kF = 0.0
            integralZone = 0
            allowableClosedloopError = 0
            maxIntegralAccumulator = 0.0
            closedLoopPeakOutput = 1.0
            closedLoopPeriod = 1
        }

        // Velocity PID slot
        slot1.apply {
            kP = 0.8
            kI = 0.0
            kD = 5.0
            kF = 1.0
            integralZone = 0
            allowableClosedloopError = 0
            maxIntegralAccumulator = 0.0
            closedLoopPeakOutput = 1.0
            closedLoopPeriod = 1
        }

        openloopRamp = kOpenLoopRamp
        closedloopRamp = 0.0

        voltageCompSaturation = kMaxVolts // Max voltage

        // TalonSRXConfiguration

        primaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder

        peakCurrentLimit = 1
        peakCurrentDuration = 1
        continuousCurrentLimit = 1
    }
}