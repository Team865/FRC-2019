@file:Suppress("MemberVisibilityCanBePrivate")

package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Input
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frckt.Subsystem
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object Drive : Subsystem(), Input {

    private val leftMaster = TalonSRX(DriveConstants.kLeftMaster)
    private val rightMaster = TalonSRX(DriveConstants.kRightMaster)

    enum class Mode {
        Percent, Velocity
    }

    var mode: Mode = Mode.Percent
    var leftDemand = 0.0
    var rightDemand = 0.0
    var leftFeedForward = 0.0
    var rightFeedForward = 0.0

    var leftPositionTicks = 0
    var rightPositionTicks = 0
    var leftVelocityTicks = 0
    var rightVelocityTicks = 0

    init {
        val leftFollowerA = VictorSPX(DriveConstants.kLeftFollowerA)
        val leftFollowerB = VictorSPX(DriveConstants.kLeftFollowerB)

        val rightFollowerA = VictorSPX(DriveConstants.kRightFollowerA)
        val rightFollowerB = VictorSPX(DriveConstants.kRightFollowerB)

        leftFollowerA.follow(leftMaster)
        leftFollowerB.follow(leftMaster)

        rightFollowerA.follow(rightMaster)
        rightFollowerB.follow(rightMaster)
    }

    override fun onDisabled() {
        leftMaster.neutralOutput()
        rightMaster.neutralOutput()
    }

    override fun onIdle() {
        mode = Mode.Percent
        leftDemand = 0.0
        rightDemand = 0.0
    }

    override fun onOutput() {
        when (mode) {
            Mode.Percent -> {
                leftMaster.set(ControlMode.PercentOutput, leftDemand)
                rightMaster.set(ControlMode.PercentOutput, rightDemand)
            }
            Mode.Velocity -> {
                leftMaster.set(ControlMode.Velocity, leftDemand, DemandType.ArbitraryFeedForward, leftFeedForward)
                rightMaster.set(ControlMode.Velocity, rightDemand, DemandType.ArbitraryFeedForward, rightFeedForward)
            }
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
}