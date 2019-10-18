package ca.warp7.frc2019.constants

import ca.warp7.frc.control.PID
import ca.warp7.frc.control.PIDControl
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration


object DriveConstants {

    // Electrical constants

    const val kLeftMaster = IOConstants.kLeftMaster
    const val kLeftFollowerA = IOConstants.kLeftFollowerA
    const val kLeftFollowerB = IOConstants.kLeftFollowerB
    const val kRightMaster = IOConstants.kRightMaster
    const val kRightFollowerA = IOConstants.kRightFollowerA
    const val kRightFollowerB = IOConstants.kRightFollowerB

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
    const val kTicksPerInch = kTicksPerRevolution / (kWheelCircumference / kInchesToMeters) // ticks/in

    // Kinematic constants

    const val kMaxVelocity = 12.0 * kFeetToMeters // m/s
    const val kMaxAcceleration = 9.0 * kFeetToMeters //  m/s^2

    // Feedback constants

    val kStraightPID: PIDControl
        get() = PIDControl(
                PID(kP = 3.0, kI = 0.00001, kD = 16.0, kF = 0.0),
                errorEpsilon = 0.07, dErrorEpsilon = 0.04, minTimeInEpsilon = 0.3,
                maxOutput = kMaxVelocity * 2
        )//meters

    val kTurnPID: PIDControl
        get() = PIDControl(
                PID(kP = 0.5, kI = 0.0001, kD = 1.5, kF = 0.0),
                errorEpsilon = 2.0, dErrorEpsilon = 1.0, minTimeInEpsilon = 0.5,
                maxOutput = kMaxVelocity * 2
        )//degrees

    val kVelocityFeedforwardPID = PID(kP = 0.8, kI = 0.0, kD = 5.0, kF = 1.0)

    // Talon configuration

    val kMasterTalonConfig
        get() = TalonSRXConfiguration().apply {
            openloopRamp = kOpenLoopRamp
            voltageCompSaturation = 12.0
            primaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder
        }
}