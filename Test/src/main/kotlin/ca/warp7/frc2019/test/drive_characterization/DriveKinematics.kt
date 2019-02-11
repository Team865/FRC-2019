package ca.warp7.frc2019.test.drive_characterization

import ca.warp7.frc.followedBy
import ca.warp7.frc.reset
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard

@Suppress("unused", "MemberVisibilityCanBePrivate")
class DriveKinematics : TimedRobot() {

    private val leftMaster: WPI_TalonSRX = WPI_TalonSRX(DriveConstants.kLeftMaster).apply {
        reset()
        setNeutralMode(NeutralMode.Brake)
        enableVoltageCompensation(true)
        enableCurrentLimit(false)
        followedBy(VictorSPX(DriveConstants.kLeftFollowerA))
        followedBy(VictorSPX(DriveConstants.kLeftFollowerB))
        selectProfileSlot(0, 0)
        selectedSensorPosition = 0
    }

    private val rightMaster: WPI_TalonSRX = WPI_TalonSRX(DriveConstants.kRightMaster).apply {
        reset()
        setNeutralMode(NeutralMode.Brake)
        enableVoltageCompensation(true)
        enableCurrentLimit(false)
        followedBy(VictorSPX(DriveConstants.kRightFollowerA))
        followedBy(VictorSPX(DriveConstants.kRightFollowerB))
        selectProfileSlot(0, 0)
        selectedSensorPosition = 0
    }

    private val tab = Shuffleboard.getTab("Drive Linear PID")

    override fun robotPeriodic() {
        tab.apply {
            add(leftMaster)
            add(rightMaster)
            add("left pos", leftMaster.selectedSensorPosition)
            add("right pos", rightMaster.selectedSensorPosition)
        }
    }

    override fun disabledInit() {
        leftMaster.disable()
        rightMaster.disable()
    }
}