@file:Suppress("MemberVisibilityCanBePrivate")

package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

object Drive : Subsystem() {

    private val leftMaster = TalonSRX(DriveConstants.kLeftMaster).also {
        VictorSPX(DriveConstants.kLeftFollowerA).follow(it)
        VictorSPX(DriveConstants.kLeftFollowerB).follow(it)
    }

    private val rightMaster = TalonSRX(DriveConstants.kRightMaster).also {
        it.inverted = true
        VictorSPX(DriveConstants.kRightFollowerA).apply { inverted = true }.follow(it)
        VictorSPX(DriveConstants.kRightFollowerB).apply { inverted = true }.follow(it)
    }

    enum class OutputMode {
        Percent, Velocity
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

    override fun onDisabled() {
        leftMaster.neutralOutput()
        rightMaster.neutralOutput()
    }

    override fun onOutput() = when (outputMode) {
        OutputMode.Percent -> {
            leftMaster.set(ControlMode.PercentOutput, leftDemand)
            rightMaster.set(ControlMode.PercentOutput, rightDemand)
        }
        OutputMode.Velocity -> {
            leftMaster.set(ControlMode.Velocity, leftDemand, DemandType.ArbitraryFeedForward, leftFeedForward)
            rightMaster.set(ControlMode.Velocity, rightDemand, DemandType.ArbitraryFeedForward, rightFeedForward)
        }
    }

    override fun onMeasure(dt: Double) {
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
    }
}