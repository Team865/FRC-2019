package ca.warp7.frc2019.constants

import com.ctre.phoenix.motorcontrol.*
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration
import com.ctre.phoenix.motorcontrol.can.VictorSPXConfiguration

@Suppress("unused")
object DriveConstants {

    const val kLeftMaster = ElectricalConstants.kDriveLeftMasterTalonId
    const val kLeftFollowerA = ElectricalConstants.kDriveLeftFollowerAVictorId
    const val kLeftFollowerB = ElectricalConstants.kDriveLeftFollowerBVictorId
    const val kRightMaster = ElectricalConstants.kDriveRightMasterTalonId
    const val kRightFollowerA = ElectricalConstants.kDriveRightFollowerAVictorId
    const val kRightFollowerB = ElectricalConstants.kDriveRightFollowerBVictorId

    const val kDifferentialDeadband = 0.2

    val kDefaultVictorSPX = VictorSPXConfiguration().apply {

        // CustomParamsConfiguration

        customParam0 = 0
        customParam1 = 0
        enableOptimizations = true

        // BaseMotorControllerConfiguration

        remoteFilter0.remoteSensorDeviceID = 0
        remoteFilter0.remoteSensorSource = RemoteSensorSource.Off

        remoteFilter1.remoteSensorDeviceID = 0
        remoteFilter1.remoteSensorSource = RemoteSensorSource.Off

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

        slot2.apply {
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

        slot3.apply {
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

        openloopRamp = 0.0
        closedloopRamp = 0.0

        peakOutputForward = 1.0
        peakOutputReverse = -1.0

        nominalOutputForward = 0.0
        nominalOutputReverse = 0.0

        neutralDeadband = 0.04

        voltageCompSaturation = 0.0
        voltageMeasurementFilter = 32

        velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms
        velocityMeasurementWindow = 64

        forwardSoftLimitThreshold = 0
        reverseSoftLimitThreshold = 0

        forwardSoftLimitEnable = false
        reverseSoftLimitEnable = false

        auxPIDPolarity = false

        motionCruiseVelocity = 0
        motionAcceleration = 0
        motionProfileTrajectoryPeriod = 0

        feedbackNotContinuous = false

        remoteSensorClosedLoopDisableNeutralOnLOS = false

        clearPositionOnLimitF = false
        clearPositionOnLimitR = false
        clearPositionOnQuadIdx = false

        limitSwitchDisableNeutralOnLOS = false
        softLimitDisableNeutralOnLOS = false

        pulseWidthPeriod_EdgesPerRot = 1
        pulseWidthPeriod_FilterWindowSz = 1

        trajectoryInterpolationEnable = true

        // VictorSPXConfiguration

        primaryPID.selectedFeedbackSensor = RemoteFeedbackDevice.RemoteSensor0
        primaryPID.selectedFeedbackCoefficient = 1.0

        auxiliaryPID.selectedFeedbackSensor = RemoteFeedbackDevice.RemoteSensor0
        auxiliaryPID.selectedFeedbackCoefficient = 1.0

        forwardLimitSwitchSource = RemoteLimitSwitchSource.Deactivated
        reverseLimitSwitchSource = RemoteLimitSwitchSource.Deactivated

        forwardLimitSwitchDeviceID = 0
        reverseLimitSwitchDeviceID = 0

        forwardLimitSwitchNormal = LimitSwitchNormal.NormallyOpen
        reverseLimitSwitchNormal = LimitSwitchNormal.NormallyOpen

        sum0Term = RemoteFeedbackDevice.RemoteSensor0
        sum1Term = RemoteFeedbackDevice.RemoteSensor0

        diff0Term = RemoteFeedbackDevice.RemoteSensor0
        diff1Term = RemoteFeedbackDevice.RemoteSensor0
    }

    val kDefaultTalonSRX = TalonSRXConfiguration().apply {

        // CustomParamsConfiguration

        customParam0 = 0
        customParam1 = 0
        enableOptimizations = true

        // BaseMotorControllerConfiguration

        remoteFilter0.remoteSensorDeviceID = 0
        remoteFilter0.remoteSensorSource = RemoteSensorSource.Off

        remoteFilter1.remoteSensorDeviceID = 0
        remoteFilter1.remoteSensorSource = RemoteSensorSource.Off

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

        slot2.apply {
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

        slot3.apply {
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

        openloopRamp = 0.0
        closedloopRamp = 0.0

        peakOutputForward = 1.0
        peakOutputReverse = -1.0

        nominalOutputForward = 0.0
        nominalOutputReverse = 0.0

        neutralDeadband = 0.04

        voltageCompSaturation = 0.0
        voltageMeasurementFilter = 32

        velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms
        velocityMeasurementWindow = 64

        forwardSoftLimitThreshold = 0
        reverseSoftLimitThreshold = 0

        forwardSoftLimitEnable = false
        reverseSoftLimitEnable = false

        auxPIDPolarity = false

        motionCruiseVelocity = 0
        motionAcceleration = 0
        motionProfileTrajectoryPeriod = 0

        feedbackNotContinuous = false

        remoteSensorClosedLoopDisableNeutralOnLOS = false

        clearPositionOnLimitF = false
        clearPositionOnLimitR = false
        clearPositionOnQuadIdx = false

        limitSwitchDisableNeutralOnLOS = false
        softLimitDisableNeutralOnLOS = false

        pulseWidthPeriod_EdgesPerRot = 1
        pulseWidthPeriod_FilterWindowSz = 1

        trajectoryInterpolationEnable = true

        // TalonSRXConfiguration

        primaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder
        primaryPID.selectedFeedbackCoefficient = 1.0

        auxiliaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder
        auxiliaryPID.selectedFeedbackCoefficient = 1.0

        forwardLimitSwitchSource = LimitSwitchSource.FeedbackConnector
        reverseLimitSwitchSource = LimitSwitchSource.FeedbackConnector

        forwardLimitSwitchDeviceID = 0
        reverseLimitSwitchDeviceID = 0

        forwardLimitSwitchNormal = LimitSwitchNormal.NormallyOpen
        reverseLimitSwitchNormal = LimitSwitchNormal.NormallyOpen

        sum0Term = FeedbackDevice.QuadEncoder
        sum1Term = FeedbackDevice.QuadEncoder

        diff0Term = FeedbackDevice.QuadEncoder
        diff1Term = FeedbackDevice.QuadEncoder

        peakCurrentLimit = 1
        peakCurrentDuration = 1
        continuousCurrentLimit = 1
    }
}