@file:Suppress("MemberVisibilityCanBePrivate")

package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.config
import ca.warp7.frc.followedBy
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

object Drive : Subsystem() {

    val leftMaster: WPI_TalonSRX = WPI_TalonSRX(DriveConstants.kLeftMaster)
            .config(DriveConstants.kMasterTalonConfig)
            .followedBy(VictorSPX(DriveConstants.kLeftFollowerA).config(DriveConstants.kFollowerVictorConfig))
            .followedBy(VictorSPX(DriveConstants.kLeftFollowerB).config(DriveConstants.kFollowerVictorConfig))

    val rightMaster: WPI_TalonSRX = WPI_TalonSRX(DriveConstants.kRightMaster)
            .config(DriveConstants.kMasterTalonConfig)
            .followedBy(VictorSPX(DriveConstants.kRightFollowerA).config(DriveConstants.kFollowerVictorConfig))
            .followedBy(VictorSPX(DriveConstants.kRightFollowerB).config(DriveConstants.kFollowerVictorConfig))

    private val differentialDrive = DifferentialDrive(leftMaster, rightMaster).apply {
        isRightSideInverted = false
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
    var prevLeftPositionTicks = 0
    var prevRightPositionTicks = 0
    var leftVelocityTicks = 0
    var rightVelocityTicks = 0

    val totalDistance
        get() = (leftPositionTicks + rightPositionTicks) / 2.0
    val totalAngle
        get() = 360 * (leftPositionTicks - rightPositionTicks) / (1024 * 2 * DriveConstants.kWheelCircumference)

    fun doWithCheckedWPIState(block: DifferentialDrive.() -> Unit) {
        if (outputMode == OutputMode.WPILibControlled) block(differentialDrive)
    }

    override fun onDisabled() {
        leftMaster.neutralOutput()
        rightMaster.neutralOutput()
    }

    override fun onOutput() = when (outputMode) {
        OutputMode.Percent -> {
            leftMaster.set(ControlMode.PercentOutput, -leftDemand)
            rightMaster.set(ControlMode.PercentOutput, rightDemand)
        }
        OutputMode.Velocity -> {
            leftMaster.set(ControlMode.Velocity, -leftDemand, DemandType.ArbitraryFeedForward, leftFeedForward)
            rightMaster.set(ControlMode.Velocity, rightDemand, DemandType.ArbitraryFeedForward, rightFeedForward)
        }
        OutputMode.Position -> {
            leftMaster.set(ControlMode.Position, -leftDemand, DemandType.ArbitraryFeedForward, leftFeedForward)
            rightMaster.set(ControlMode.Position, rightDemand, DemandType.ArbitraryFeedForward, rightFeedForward)
        }
        OutputMode.WPILibControlled -> Unit
    }

    override fun onMeasure(dt: Double) {
        prevLeftPositionTicks = leftPositionTicks
        prevRightPositionTicks = rightPositionTicks
        leftPositionTicks = leftMaster.selectedSensorPosition
        rightPositionTicks = rightMaster.selectedSensorPosition
        leftVelocityTicks = leftMaster.selectedSensorVelocity
        rightVelocityTicks = rightMaster.selectedSensorVelocity
    }

    override fun onZeroSensors() {
        leftMaster.selectedSensorPosition = 0
        rightMaster.selectedSensorPosition = 0
    }

    override fun onUpdateShuffleboard(container: ShuffleboardContainer) {
        container
                .add(differentialDrive)
                .withWidget(BuiltInWidgets.kDifferentialDrive)
    }
}