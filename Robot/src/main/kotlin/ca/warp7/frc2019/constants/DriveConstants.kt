package ca.warp7.frc2019.constants

import ca.warp7.frc.PID
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration


object DriveConstants {

    // Electrical constants

    const val kLeftMaster = ElectricalConstants.kDriveLeftMasterTalonId
    const val kLeftFollowerA = ElectricalConstants.kDriveLeftFollowerAVictorId
    const val kLeftFollowerB = ElectricalConstants.kDriveLeftFollowerBVictorId
    const val kRightMaster = ElectricalConstants.kDriveRightMasterTalonId
    const val kRightFollowerA = ElectricalConstants.kDriveRightFollowerAVictorId
    const val kRightFollowerB = ElectricalConstants.kDriveRightFollowerBVictorId

    // Teleop Control constants

    const val kOpenLoopRamp = 0.15 // s
    const val kDifferentialDeadband = 0.2
    const val kQuickTurnMultiplier = 0.7

    // Unit conversion constants

    const val kFeetToMeters: Double = 0.3048
    const val kInchesToMeters: Double = 0.0254
    const val kFeetToInches: Double = 12.0

    // Dimension Constants

    const val kWheelRadius = 2.95 * kInchesToMeters // m
    const val kWheelCircumference = kWheelRadius * 2 * Math.PI // m
    const val kTurningDiameter = 24.75 * kInchesToMeters // m
    const val kTurningCircumference = kTurningDiameter * Math.PI // m

    // Talon unit constants

    const val kTicksPerRevolution = 1024 // ticks/rev
    const val kTicksPerRadian = kTicksPerRevolution / (2 * Math.PI)
    const val kTicksPerInch = kTicksPerRevolution / (kWheelCircumference / kInchesToMeters) // ticks/in
    const val kTicksPerFootPer100ms = 12 * kTicksPerInch * 0.1 // ticks/(ft/100ms)
    const val kTicksPerMeterPer100ms = kTicksPerInch / kInchesToMeters * 0.1 // ticks(m/100ms)
    const val kMaxTalonPIDOutput = 1023.0

    // Kinematic constants

    const val kMaxVelocity = 12.0 * kFeetToMeters // m/s
    const val kMaxAcceleration = 9.0 * kFeetToMeters //  m/s^2
    const val kMaxFreeSpeed = 16.5 * kFeetToMeters// m/s

    private const val kScrubFactor = 1.45
    const val kEffectiveWheelBaseRadius = kTurningDiameter / 2 * kScrubFactor // m

    // Dynamic constants

    const val kMaxVolts = 12.0 // V
    const val kFrictionVoltage = 1.0 // V
    const val kLinearInertia = 60.0 // kg
    const val kAngularInertia = 10.0 // kg * m^2
    const val kAngularDrag = 20.0 // (N * m) / (rad/s)
    const val kSpeedPerVolt = (kMaxFreeSpeed / kWheelRadius) / (kMaxVolts - kFrictionVoltage) // (rad/s) / V
    const val kA = 83.0 // // (rad/s^2) / V
    const val kTorquePerVolt = 0.5 * kWheelRadius * kWheelRadius * kLinearInertia * kA  // (N * m) / V

    // Feedback constants

    val kStraightPID: PID
        get() = PID(
                kP = 3.0, kI = 0.00001, kD = 16.0, kF = 0.0,
                errorEpsilon = 0.07, dErrorEpsilon = 0.04, minTimeInEpsilon = 0.3,
                maxOutput = kMaxVelocity * 2
        )//meters

    val kTurnPID: PID
        get() = PID(
                kP = 0.5, kI = 0.0001, kD = 1.5, kF = 0.0,
                errorEpsilon = 2.0, dErrorEpsilon = 1.0, minTimeInEpsilon = 0.5,
                maxOutput = kMaxVelocity * 2
        )//degrees

    val kVelocityFeedforwardPID = PID(kP = 0.8, kI = 0.0, kD = 5.0, kF = 1.0)
    val kTrajectoryPID = PID(kP = 1.0, kI = 0.0, kD = 10.0, kF = 0.0)

    const val kPoseX = 5.0
    const val kPoseY = 5.0
    const val kPoseTheta = 1.0

    const val kAngleP = 5.0

    // Equation 5.12 from https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
    const val kRamseteBeta = 2.0  // Correction coefficient, β > 0
    const val kRamseteZeta = 0.7  // Damping coefficient, 0 < ζ < 1

    // Talon configuration

    val kMasterTalonConfig
        get() = TalonSRXConfiguration().apply {
            openloopRamp = kOpenLoopRamp
            voltageCompSaturation = kMaxVolts
            primaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder
        }
}