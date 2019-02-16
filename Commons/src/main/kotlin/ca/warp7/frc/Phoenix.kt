@file:Suppress("unused")

package ca.warp7.frc

import com.ctre.phoenix.motorcontrol.*
import com.ctre.phoenix.motorcontrol.can.*
import edu.wpi.first.wpilibj.SpeedController

fun <T : TalonSRX> T.config(config: TalonSRXConfiguration) = apply { configAllSettings(config) }

fun <T : VictorSPX> T.config(config: VictorSPXConfiguration) = apply { configAllSettings(config) }

fun <T : BaseMotorController> T.followedBy(vararg other: BaseMotorController) = apply { other.forEach { it.reset().follow(this) } }

fun <T : BaseMotorController> T.reset() = apply {
    configFactoryDefault()
    selectProfileSlot(0, 0)
    setNeutralMode(NeutralMode.Brake)
}

private class UnsafeSpeedController(val base: BaseMotorController) : SpeedController {

    var lastSpeed = 0.0

    override fun getInverted(): Boolean = base.inverted

    override fun pidWrite(output: Double) = set(output)

    override fun stopMotor() = base.neutralOutput()

    override fun get(): Double = lastSpeed

    override fun disable() = base.neutralOutput()

    override fun set(speed: Double) {
        lastSpeed = speed
        base.set(ControlMode.PercentOutput, speed)
    }

    override fun setInverted(isInverted: Boolean) {
        base.inverted = isInverted
    }
}

private class LazyTalonSRX(deviceNumber: Int) : TalonSRX(deviceNumber) {

    var previousMode = ControlMode.PercentOutput
    var previousDemand0 = 0.0
    var previousDemand1Type = DemandType.Neutral
    var previousDemand1 = 0.0

    override fun set(mode: ControlMode, demand0: Double, demand1Type: DemandType, demand1: Double) {
        if (mode != previousMode
                || !demand0.epsilonEquals(previousDemand0, 1E-12)
                || demand1Type != previousDemand1Type
                || !demand1.epsilonEquals(previousDemand1, 1E-12)) {
            previousMode = mode
            previousDemand0 = demand0
            previousDemand1Type = demand1Type
            previousDemand1 = demand1
            super.set(mode, demand0, demand1Type, demand1)
        }
    }
}

fun lazyTalonSRX(
        id: Int,
        config: TalonSRXConfiguration? = null,
        neutralMode: NeutralMode = NeutralMode.Brake,
        voltageCompensation: Boolean = false,
        currentLimit: Boolean = false
): TalonSRX = LazyTalonSRX(id).apply {
    if (config == null) configFactoryDefault()
    else configAllSettings(config)
    setNeutralMode(neutralMode)
    enableVoltageCompensation(voltageCompensation)
    enableCurrentLimit(currentLimit)
    selectedSensorPosition = 0
    selectProfileSlot(0, 0)
}

fun <T : BaseMotorController> T.wpi(): SpeedController = UnsafeSpeedController(this)

val kDefaultVictorSPX
    get() = VictorSPXConfiguration().apply {

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

val kDefaultTalonSRX
    get() = TalonSRXConfiguration().apply {

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