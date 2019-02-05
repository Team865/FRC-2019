package ca.warp7.frc2019.constants

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration


@Suppress("unused")
object LiftConstants {
    const val kMaster = ElectricalConstants.kLiftMasterTalonId
    const val kFollower = ElectricalConstants.kLiftFollowerVictorId

    private const val kRotationsPerTick = 4096
    private const val kDrumRadiusInches = 1.5
    private const val kDrumCircumfrence = 2 * kDrumRadiusInches * Math.PI
    const val kInchesPerTick = kRotationsPerTick * kDrumCircumfrence

    const val kHomeHeightInches = 0.0 //TODO fix this value

    const val kMaxBaseAcceleration = 1.0 //TODO find actual max acceleration 65m/s^2 ??
    const val kMaxVelocityInchesPerSecond = 74.0 //TODO find out if this is true

    const val kHallEffect = ElectricalConstants.kLiftHallEffectSensorDIO

    val kMasterTalonConfiguration = TalonSRXConfiguration().apply {

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

        openloopRamp = 0.0
        closedloopRamp = 0.0

        neutralDeadband = 0.04

        voltageCompSaturation = 0.0

        // TalonSRXConfiguration

        primaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder
        primaryPID.selectedFeedbackCoefficient = 1.0

        peakCurrentLimit = 1
        peakCurrentDuration = 1
        continuousCurrentLimit = 1
    }
}