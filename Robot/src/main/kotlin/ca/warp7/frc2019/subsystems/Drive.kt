@file:Suppress("MemberVisibilityCanBePrivate")

package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.config
import ca.warp7.frc.followedBy
import ca.warp7.frc.wpi
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

object Drive : Subsystem() {

    val leftMaster: TalonSRX = TalonSRX(DriveConstants.kLeftMaster)
            .config(DriveConstants.kMasterTalonConfig)
            .followedBy(VictorSPX(DriveConstants.kLeftFollowerA))
            .followedBy(VictorSPX(DriveConstants.kLeftFollowerB))

    val rightMaster: TalonSRX = TalonSRX(DriveConstants.kRightMaster)
            .config(DriveConstants.kMasterTalonConfig)
            .followedBy(VictorSPX(DriveConstants.kRightFollowerA))
            .followedBy(VictorSPX(DriveConstants.kRightFollowerB))

    val wpiDrive: DifferentialDrive = DifferentialDrive(leftMaster.wpi, rightMaster.wpi).apply {
        setDeadband(DriveConstants.kDifferentialDeadband)
        setQuickStopAlpha(DifferentialDrive.kDefaultQuickStopAlpha)
        setQuickStopThreshold(DifferentialDrive.kDefaultQuickStopThreshold)
    }

    enum class OutputMode {
        Percent, Velocity, Position, WPILibControlled
    }

    var outputMode: OutputMode = OutputMode.Percent

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
        OutputMode.Percent -> {
            leftMaster.set(ControlMode.PercentOutput, leftDemand1)
            rightMaster.set(ControlMode.PercentOutput, rightDemand)
        }
        OutputMode.Velocity -> {
            leftMaster.set(ControlMode.Velocity, leftDemand1, DemandType.ArbitraryFeedForward, leftFeedForward1)
            rightMaster.set(ControlMode.Velocity, rightDemand, DemandType.ArbitraryFeedForward, rightFeedForward)
        }
        OutputMode.Position -> {
            leftMaster.set(ControlMode.Position, leftDemand1, DemandType.ArbitraryFeedForward, leftFeedForward1)
            rightMaster.set(ControlMode.Position, rightDemand, DemandType.ArbitraryFeedForward, rightFeedForward)
        }
        OutputMode.WPILibControlled -> Unit
    }

    override fun onMeasure(dt: Double) {
        leftPositionTicks = leftMaster.selectedSensorPosition
        rightPositionTicks = rightMaster.selectedSensorPosition
        leftVelocityTicks = leftMaster.selectedSensorVelocity
        rightVelocityTicks = rightMaster.selectedSensorVelocity
    }

    override fun onUpdateShuffleboard(container: ShuffleboardContainer) {
        container.apply {
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
}