package ca.warp7.frc2019.constants

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration

object LiftConstants {
    const val kMaster = ElectricalConstants.kLiftMasterTalonId
    const val kFollower = ElectricalConstants.kLiftFollowerVictorId
    const val kHallEffect = ElectricalConstants.kLiftHallEffectSensorDIO

    private const val kRopeDiameter = 3 / 25.4

    private const val kDrumDiameter = 2.0 // + kRopeDiameter // Inches
    private const val kDrumCircumference = kDrumDiameter * Math.PI
    const val kTicksPerInch = COTSConstants.MagEncoder.kTicksPerRevolution / kDrumCircumference

    const val kHomeHeightInches = 18.5

    const val kMaxBaseAcceleration = 3 //TODO find actual max acceleration 65m/s^2 ??
    const val kMaxVelocityInchesPerSecond = 74.0 //TODO find out if this is true

    const val kPrimaryFeedforward = 0.13
    const val kManualControlScale = 0.6

    const val kStoppedVelocityThreshold = 64 // TODO
    const val kStoppedCurrentEpsilon = 0.1 // TODO
    const val kPurePursuitPositionGain = 0.4 // TODO
    const val kMaximumSetpoint = 84.0 // TODO
    const val kEpsilon = 1E-9
    const val kAccelerationMeasurementFrames = 5
    const val kMaxAcceleration = kMaxBaseAcceleration

    const val kMaxHeightEncoderTicks = 42268.0

    const val kPIDDeadSpotHeight = 2.5
    const val kMoveToBottomDemand = -0.06

    val kMasterTalonConfig = TalonSRXConfiguration().apply {

        // TODO Position PID slot
        slot0.apply {
            kP = 0.2
            kI = 0.0
            kD = 0.1
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

        openloopRamp = 0.15
        closedloopRamp = 0.15

        neutralDeadband = 0.04

        voltageCompSaturation = 12.0

        // TalonSRXConfiguration

        primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative

        peakCurrentLimit = 1
        peakCurrentDuration = 1
        continuousCurrentLimit = 1
    }
}