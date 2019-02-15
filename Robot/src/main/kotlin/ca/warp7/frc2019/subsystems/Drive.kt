@file:Suppress("MemberVisibilityCanBePrivate")

package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.config
import ca.warp7.frc.followedBy
import ca.warp7.frc.wpi
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive.OutputMode.*
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets

object Drive : Subsystem() {

    val leftMaster: TalonSRX = TalonSRX(DriveConstants.kLeftMaster).apply {
        config(DriveConstants.kMasterTalonConfig)
        setNeutralMode(NeutralMode.Brake)
        enableVoltageCompensation(true)
        enableCurrentLimit(false)
        followedBy(VictorSPX(DriveConstants.kLeftFollowerA))
        followedBy(VictorSPX(DriveConstants.kLeftFollowerB))
        selectedSensorPosition = 0
    }

    val rightMaster: TalonSRX = TalonSRX(DriveConstants.kRightMaster).apply {
        config(DriveConstants.kMasterTalonConfig)
        setNeutralMode(NeutralMode.Brake)
        enableVoltageCompensation(true)
        enableCurrentLimit(false)
        followedBy(VictorSPX(DriveConstants.kRightFollowerA))
        followedBy(VictorSPX(DriveConstants.kRightFollowerB))
        selectedSensorPosition = 0
    }

    val wpiDrive: DifferentialDrive = DifferentialDrive(leftMaster.wpi(), rightMaster.wpi()).apply {
        setDeadband(DriveConstants.kDifferentialDeadband)
        isSafetyEnabled = false
    }

    enum class OutputMode {
        Percent, Velocity, Position, WPILibControlled
    }

    var outputMode = Percent
        set(value) {
            if (field != value) when (value) {
                Position -> {
                    leftMaster.selectProfileSlot(0, 0)
                    rightMaster.selectProfileSlot(0, 0)
                }
                Velocity -> {
                    leftMaster.selectProfileSlot(1, 0)
                    rightMaster.selectProfileSlot(1, 0)
                }
                else -> Unit
            }
            field = value
        }

    var leftDemand = 0.0
    var rightDemand = 0.0
    var leftFeedForward = 0.0
    var rightFeedForward = 0.0

    var leftPositionTicks = 0
    var rightPositionTicks = 0
    var leftVelocityTicks = 0
    var rightVelocityTicks = 0

    val totalDistance
        get() = (leftPositionTicks + rightPositionTicks) / 2.0
    val totalAngle
        get() = 360 * (leftPositionTicks - rightPositionTicks) / (1024 * 2 * DriveConstants.kWheelCircumference)

    private val leftDemand1 get() = leftDemand * -1
    private val leftFeedForward1 get() = leftFeedForward * -1

    override fun onDisabled() {
        leftMaster.neutralOutput()
        rightMaster.neutralOutput()
    }

    override fun onOutput() = when (outputMode) {
        Percent -> {
            leftMaster.set(ControlMode.PercentOutput, leftDemand1)
            rightMaster.set(ControlMode.PercentOutput, rightDemand)
        }
        Velocity -> {
            leftMaster.set(ControlMode.Velocity, leftDemand1, DemandType.ArbitraryFeedForward, leftFeedForward1)
            rightMaster.set(ControlMode.Velocity, rightDemand, DemandType.ArbitraryFeedForward, rightFeedForward)
        }
        Position -> {
            leftMaster.set(ControlMode.Position, leftDemand1, DemandType.ArbitraryFeedForward, leftFeedForward1)
            rightMaster.set(ControlMode.Position, rightDemand, DemandType.ArbitraryFeedForward, rightFeedForward)
        }
        WPILibControlled -> Unit
    }

    override fun onMeasure(dt: Double) {
//        leftPositionTicks = leftMaster.selectedSensorPosition
//        rightPositionTicks = rightMaster.selectedSensorPosition
//        leftVelocityTicks = leftMaster.selectedSensorVelocity
//        rightVelocityTicks = rightMaster.selectedSensorVelocity
        DriveMotionPlanner.updateMeasurements(dt)
    }

    override fun onPostUpdate() = shuffleboard {
        add("Output Mode", outputMode.name)
        add("Left Demand", leftDemand)
        add("Left Feedforward", leftFeedForward)
        add("Right Demand", rightDemand)
        add("Right Feedforward", rightFeedForward)
        add("Left Position", leftPositionTicks)
        add("Right Position", rightPositionTicks)
        add("Left Velocity", leftVelocityTicks)
        add("Right Velocity", rightVelocityTicks)
        add(wpiDrive).withWidget(BuiltInWidgets.kDifferentialDrive)
    }
}