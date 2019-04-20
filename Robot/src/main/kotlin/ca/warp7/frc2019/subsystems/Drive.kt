@file:Suppress("MemberVisibilityCanBePrivate")

package ca.warp7.frc2019.subsystems

import ca.warp7.frc.PID
import ca.warp7.frc.control.Subsystem
import ca.warp7.frc.control.followedBy
import ca.warp7.frc.control.setPID
import ca.warp7.frc.control.talonSRX
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object Drive : Subsystem() {

    val leftMaster: TalonSRX = talonSRX(DriveConstants.kLeftMaster, DriveConstants.kMasterTalonConfig)
            .followedBy(VictorSPX(DriveConstants.kLeftFollowerA), VictorSPX(DriveConstants.kLeftFollowerB))

    val rightMaster: TalonSRX = talonSRX(DriveConstants.kRightMaster, DriveConstants.kMasterTalonConfig)
            .followedBy(VictorSPX(DriveConstants.kRightFollowerA), VictorSPX(DriveConstants.kRightFollowerB))

    var controlMode = ControlMode.PercentOutput

    var leftDemand = 0.0
    var rightDemand = 0.0
    var leftFeedforward = 0.0
    var rightFeedforward = 0.0

    var leftPosition = 0
    var rightPosition = 0
    var leftVelocity = 0
    var rightVelocity = 0

    fun setPID(pid: PID) {
        leftMaster.setPID(pid)
        rightMaster.setPID(pid)
    }

    override fun onDisabled() {
        leftDemand = 0.0
        rightDemand = 0.0
        leftFeedforward = 0.0
        rightFeedforward = 0.0
        leftMaster.neutralOutput()
        rightMaster.neutralOutput()
    }

    override fun onOutput() {
        leftMaster.set(controlMode, leftDemand, DemandType.ArbitraryFeedForward, leftFeedforward)
        rightMaster.set(controlMode, -rightDemand, DemandType.ArbitraryFeedForward, -rightFeedforward)
    }

    override fun onMeasure(dt: Double) {
//        leftPosition = leftMaster.selectedSensorPosition
//        rightPosition = rightMaster.selectedSensorPosition * -1
//        leftVelocity = leftMaster.selectedSensorVelocity
//        rightVelocity = rightMaster.selectedSensorVelocity * -1
        DriveMotionPlanner.updateMeasurements(dt)
    }

    override fun onPostUpdate() {
//        put("x", DriveMotionPlanner.robotState.translation.x)
//        put("y", DriveMotionPlanner.robotState.translation.y)
//        put("yaw", DriveMotionPlanner.robotState.rotation.degrees)
//        put("Left Demand", leftDemand)
//        put("Right Demand", rightDemand)
//        put("Left Velocity", leftVelocity)
//        put("Right Velocity", rightVelocity)
//        put("Left Position", leftPosition)
//        put("Right Position", rightPosition)
    }
}