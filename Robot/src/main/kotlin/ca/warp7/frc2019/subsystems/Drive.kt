@file:Suppress("MemberVisibilityCanBePrivate")

package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.followedBy
import ca.warp7.frc.lazyTalonSRX
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object Drive : Subsystem() {

    private val leftMaster: TalonSRX = lazyTalonSRX(
            id = DriveConstants.kLeftMaster,
            config = DriveConstants.kMasterTalonConfig,
            voltageCompensation = true,
            currentLimit = false
    ).followedBy(
            VictorSPX(DriveConstants.kLeftFollowerA),
            VictorSPX(DriveConstants.kLeftFollowerB)
    )

    private val rightMaster: TalonSRX = lazyTalonSRX(
            id = DriveConstants.kRightMaster,
            config = DriveConstants.kMasterTalonConfig,
            voltageCompensation = true,
            currentLimit = false
    ).followedBy(
            VictorSPX(DriveConstants.kRightFollowerA),
            VictorSPX(DriveConstants.kRightFollowerB)
    )

    val motionPlanner: DriveMotionPlanner = DriveMotionPlanner

    var controlMode = ControlMode.PercentOutput
        set(value) {
            if (field != value) when (value) {
                ControlMode.Position -> {
                    leftMaster.selectProfileSlot(0, 0)
                    rightMaster.selectProfileSlot(0, 0)
                }
                ControlMode.Velocity -> {
                    leftMaster.selectProfileSlot(1, 0)
                    rightMaster.selectProfileSlot(1, 0)
                }
                else -> Unit
            }
            field = value
        }

    var leftDemand = 0.0
    var rightDemand = 0.0
    var leftFeedforward = 0.0
    var rightFeedforward = 0.0

    var leftPositionTicks = 0
    var rightPositionTicks = 0
    var leftVelocityTicks = 0
    var rightVelocityTicks = 0

    private val reversedLeftDemand: Double get() = leftDemand * -1
    private val reversedLeftFeedforward: Double get() = leftFeedforward * -1

    override fun onDisabled() {
        leftMaster.neutralOutput()
        rightMaster.neutralOutput()
    }

    override fun onOutput() {
        leftMaster.set(controlMode, reversedLeftDemand, DemandType.ArbitraryFeedForward, reversedLeftFeedforward)
        rightMaster.set(controlMode, rightDemand, DemandType.ArbitraryFeedForward, rightFeedforward)
    }

    override fun onMeasure(dt: Double) {
        leftPositionTicks = -leftMaster.selectedSensorPosition * -1
        rightPositionTicks = rightMaster.selectedSensorPosition
        leftVelocityTicks = leftMaster.selectedSensorVelocity * -1
        rightVelocityTicks = rightMaster.selectedSensorVelocity
        DriveMotionPlanner.updateMeasurements(dt)
    }

    override fun onPostUpdate() {
        graph("Left Demand", leftDemand)
        graph("Left Feedforward", leftFeedforward)
        graph("Right Demand", rightDemand)
        graph("Right Feedforward", rightFeedforward)
        graph("Left Velocity", leftVelocityTicks)
        graph("Right Velocity", rightVelocityTicks)
        put("Left Position", leftPositionTicks)
        put("Right Position", rightPositionTicks)
    }
}