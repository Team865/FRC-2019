package ca.warp7.frc2019.test.drive_linear_pid

import ca.warp7.frc.config
import ca.warp7.frc.followedBy
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard

@Suppress("unused")
class DriveLinearPID : TimedRobot() {

    private val leftMaster: WPI_TalonSRX = WPI_TalonSRX(DriveConstants.kLeftMaster).apply {
        config(DriveConstants.kMasterTalonConfig)
        setNeutralMode(NeutralMode.Brake)
        enableVoltageCompensation(true)
        enableCurrentLimit(false)
        followedBy(VictorSPX(DriveConstants.kLeftFollowerA))
        followedBy(VictorSPX(DriveConstants.kLeftFollowerB))
        selectedSensorPosition = 0
    }

    private val rightMaster: WPI_TalonSRX = WPI_TalonSRX(DriveConstants.kRightMaster).apply {
        config(DriveConstants.kMasterTalonConfig)
        setNeutralMode(NeutralMode.Brake)
        enableVoltageCompensation(true)
        enableCurrentLimit(false)
        followedBy(VictorSPX(DriveConstants.kRightFollowerA))
        followedBy(VictorSPX(DriveConstants.kRightFollowerB))
        selectedSensorPosition = 0
    }

    private val target = 256 * 10.0 // 60 inches

    override fun robotPeriodic() {
        Shuffleboard.getTab("Drive Linear PID").apply {
            add(leftMaster)
            add(rightMaster)
            add("left pos", leftMaster.selectedSensorPosition)
            add("right pos", rightMaster.selectedSensorPosition)
        }
    }

    override fun autonomousPeriodic() {
        leftMaster.set(ControlMode.Position, target)
        rightMaster.set(ControlMode.Position, target)
    }
}