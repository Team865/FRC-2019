@file:Suppress("unused")

package ca.warp7.frc

import com.ctre.phoenix.motorcontrol.*
import com.ctre.phoenix.motorcontrol.can.*
import edu.wpi.first.wpilibj.SpeedController

fun <T : TalonSRX> T.config(config: TalonSRXConfiguration) = apply { configAllSettings(config) }

fun <T : VictorSPX> T.config(config: VictorSPXConfiguration) = apply { configAllSettings(config) }

fun <T : BaseMotorController> T.followedBy(vararg other: BaseMotorController) =
        apply { other.forEach { it.reset().follow(this) } }

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
        currentLimit: Boolean = false,
        inverted: Boolean = false
): TalonSRX = LazyTalonSRX(id).apply {
    if (config == null) configFactoryDefault(50)
    else configAllSettings(config, 50)
    setNeutralMode(neutralMode)
    enableVoltageCompensation(voltageCompensation)
    enableCurrentLimit(currentLimit)
    setInverted(inverted)
    selectedSensorPosition = 0
    selectProfileSlot(0, 0)
}

private class LazyVictorSPX(deviceNumber: Int) : VictorSPX(deviceNumber) {

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

fun lazyVictorSPX(
        id: Int,
        config: VictorSPXConfiguration? = null,
        neutralMode: NeutralMode = NeutralMode.Brake,
        voltageCompensation: Boolean = false,
        inverted: Boolean = false
): VictorSPX = LazyVictorSPX(id).apply {
    if (config == null) configFactoryDefault(50)
    else configAllSettings(config, 50)
    setNeutralMode(neutralMode)
    enableVoltageCompensation(voltageCompensation)
    setInverted(inverted)
    selectProfileSlot(0, 0)
}

fun <T : BaseMotorController> T.wpi(): SpeedController = UnsafeSpeedController(this)

val kDefaultTalonSRX
    get() = TalonSRXConfiguration().apply {

        // CustomParamsConfiguration

        /**
         * Custom Param 0
         */
        customParam0 = 0

        /**
         * Custom Param 1
         */
        customParam1 = 0

        /**
         * Enable optimizations for ConfigAll (defaults true)
         */
        enableOptimizations = true

        // BaseMotorControllerConfiguration

        /**
         * Configuration for RemoteFilter 0
         */

        /**
         * Remote Sensor's device ID
         */
        remoteFilter0.remoteSensorDeviceID = 0

        /**
         * The remote sensor device and signal type to bind.
         */
        remoteFilter0.remoteSensorSource = RemoteSensorSource.Off

        /**
         * Configuration for RemoteFilter 1
         */

        remoteFilter1.remoteSensorDeviceID = 0

        remoteFilter1.remoteSensorSource = RemoteSensorSource.Off

        /**
         * Configuration for slot 0
         */
        slot0.apply {
            /**
             * P Gain
             *
             * This is multiplied by closed loop error in sensor units.
             * Note the closed loop output interprets a final value of 1023 as full output.
             * So use a gain of '0.25' to get full output if err is 4096u (Mag Encoder 1 rotation)
             */
            kP = 0.0

            /**
             * I Gain
             *
             * This is multiplied by accumulated closed loop error in sensor units every PID Loop.
             * Note the closed loop output interprets a final value of 1023 as full output.
             * So use a gain of '0.00025' to get full output if err is 4096u for 1000 loops (accumulater holds 4,096,000),
             * [which is equivalent to one CTRE mag encoder rotation for 1000 milliseconds].
             */
            kI = 0.0

            /**
             * D Gain
             *
             * This is multiplied by derivative error (sensor units per PID loop, typically 1ms).
             * Note the closed loop output interprets a final value of 1023 as full output.
             * So use a gain of '250' to get full output if derr is 4096u (Mag Encoder 1 rotation) per 1000 loops (typ 1 sec)
             */
            kD = 0.0

            /**
             * F Gain
             *
             * See documentation for calculation details.
             * If using velocity, motion magic, or motion profile,
             * use (1023 * duty-cycle / sensor-velocity-sensor-units-per-100ms).
             *
             */
            kF = 0.0

            /**
             * Integral zone (in native units)
             *
             * If the (absolute) closed-loop error is outside of this zone, integral
             * accumulator is automatically cleared. This ensures than integral wind up
             * events will stop after the sensor gets far enough from its target.
             *
             */
            integralZone = 0

            /**
             * Allowable closed loop error to neutral (in native units)
             */
            allowableClosedloopError = 0

            /**
             * Max integral accumulator (in native units)
             */
            maxIntegralAccumulator = 0.0

            /**
             * Peak output from closed loop [0,1]
             */
            closedLoopPeakOutput = 1.0

            /**
             * Desired period of closed loop [1,64]ms
             */
            closedLoopPeriod = 1
        }

        /**
         * Configuration for slot 1
         */
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

        /**
         * Configuration for slot 2
         */
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

        /**
         * Configuration for slot 3
         */
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

        /**
         * Seconds to go from 0 to full in open loop
         */
        openloopRamp = 0.0

        /**
         * Seconds to go from 0 to full in closed loop
         */
        closedloopRamp = 0.0


        /**
         * Peak output in forward direction [0,1]
         */
        peakOutputForward = 1.0

        /**
         * Peak output in reverse direction [-1,0]
         */
        peakOutputReverse = -1.0

        /**
         * Nominal/Minimum output in forward direction [0,1]
         */
        nominalOutputForward = 0.0

        /**
         * Nominal/Minimum output in reverse direction [-1,0]
         */
        nominalOutputReverse = 0.0

        /**
         * Neutral deadband [0.001, 0.25]
         */
        neutralDeadband = 0.04

        /**
         * This is the max voltage to apply to the hbridge when voltage
         * compensation is enabled.  For example, if 10 (volts) is specified
         * and a TalonSRX is commanded to 0.5 (PercentOutput, closed-loop, etc)
         * then the TalonSRX will attempt to apply a duty-cycle to produce 5V.
         */
        voltageCompSaturation = 0.0

        /**
         * Number of samples in rolling average for voltage
         */
        voltageMeasurementFilter = 32

        /**
         * Desired period for velocity measurement
         */
        velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms

        /**
         * Desired window for velocity measurement
         */
        velocityMeasurementWindow = 64

        /**
         * Threshold for soft limits in forward direction (in raw sensor units)
         */
        forwardSoftLimitThreshold = 0

        /**
         * Threshold for soft limits in reverse direction (in raw sensor units)
         */
        reverseSoftLimitThreshold = 0

        /**
         * Enable forward soft limit
         */
        forwardSoftLimitEnable = false

        /**
         * Enable reverse soft limit
         */
        reverseSoftLimitEnable = false

        /**
         * PID polarity inversion
         *
         * Standard Polarity:
         *    Primary Output = PID0 + PID1,
         *    Auxiliary Output = PID0 - PID1,
         *
         * Inverted Polarity:
         *    Primary Output = PID0 - PID1,
         *    Auxiliary Output = PID0 + PID1,
         */
        auxPIDPolarity = false

        /**
         * Motion Magic cruise velocity in raw sensor units per 100 ms.
         */
        motionCruiseVelocity = 0

        /**
         * Motion Magic acceleration in (raw sensor units per 100 ms) per second.
         */
        motionAcceleration = 0

        /**
         * Motion profile base trajectory period in milliseconds.
         *
         * The period specified in a trajectory point will be
         * added on to this value
         */
        motionProfileTrajectoryPeriod = 0

        /**
         * Determine whether feedback sensor is continuous or not
         */
        feedbackNotContinuous = false

        /**
         * Disable neutral'ing the motor when remote sensor is lost on CAN bus
         */
        remoteSensorClosedLoopDisableNeutralOnLOS = false

        /**
         * Clear the position on forward limit
         */
        clearPositionOnLimitF = false

        /**
         * Clear the position on reverse limit
         */
        clearPositionOnLimitR = false

        /**
         * Clear the position on index
         */
        clearPositionOnQuadIdx = false

        /**
         * Disable neutral'ing the motor when remote limit switch is lost on CAN bus
         */
        limitSwitchDisableNeutralOnLOS = false

        /**
         * Disable neutral'ing the motor when remote soft limit is lost on CAN bus
         */
        softLimitDisableNeutralOnLOS = false

        /**
         * Number of edges per rotation for a tachometer sensor
         */
        pulseWidthPeriod_EdgesPerRot = 1

        /**
         * Desired window size for a tachometer sensor
         */
        pulseWidthPeriod_FilterWindowSz = 1

        /**
         * Enable motion profile trajectory point interpolation (defaults to true).
         */
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
