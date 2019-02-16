package ca.warp7.frc2019.constants

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration


object DriveConstants {

    /*
    ==================
    Physical constants
    ==================
    */

    private const val kWheelDiameter = 6.0 // Inches
    const val kWheelCircumference = kWheelDiameter * Math.PI // Inches

    private const val kWheelBase = 12.0 // Distance between left and right wheels in inches TODO
    const val kTurningCircumference = kWheelBase * Math.PI // The circumference the wheel base turns across in inches

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

    const val kRampSecondsFromNeutralToFull = 0.5


    val kMasterTalonConfig = TalonSRXConfiguration().apply {

        // TODO Position PID slot
        slot0.apply {
            kP = 0.0
            kI = 0.0
            kD = 0.0
            kF = 0.0
            integralZone = 0
            allowableClosedloopError = 0
            maxIntegralAccumulator = 0.0
            closedLoopPeakOutput = 1.0
            closedLoopPeriod = 1
        }

        // TODO Velocity PID slot
        slot1.apply {
            kP = 0.0
            kI = 0.0
            kD = 0.0
            kF = 0.0
            integralZone = 0
            allowableClosedloopError = 0
            maxIntegralAccumulator = 0.0
            closedLoopPeakOutput = 1.0
            closedLoopPeriod = 1
        }

        openloopRamp = 1.0
        closedloopRamp = 0.0

        neutralDeadband = 0.04

        voltageCompSaturation = 12.0 // Max voltage

        // TalonSRXConfiguration

        primaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder

        peakCurrentLimit = 1
        peakCurrentDuration = 1
        continuousCurrentLimit = 1
    }
}