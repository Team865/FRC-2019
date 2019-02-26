package ca.warp7.frc2019.test.drive

import ca.warp7.frc.config
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.followedBy
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard

@Suppress("unused", "MemberVisibilityCanBePrivate")
class DriveLinearPID : TimedRobot() {

    private val leftMaster: WPI_TalonSRX = WPI_TalonSRX(DriveConstants.kLeftMaster).apply {
        config(DriveConstants.kMasterTalonConfig)
        setNeutralMode(NeutralMode.Brake)
        enableVoltageCompensation(true)
        enableCurrentLimit(false)
        followedBy(VictorSPX(DriveConstants.kLeftFollowerA))
        followedBy(VictorSPX(DriveConstants.kLeftFollowerB))
        selectProfileSlot(0, 0)
        selectedSensorPosition = 0
    }

    private val rightMaster: WPI_TalonSRX = WPI_TalonSRX(DriveConstants.kRightMaster).apply {
        config(DriveConstants.kMasterTalonConfig)
        setNeutralMode(NeutralMode.Brake)
        enableVoltageCompensation(true)
        enableCurrentLimit(false)
        followedBy(VictorSPX(DriveConstants.kRightFollowerA))
        followedBy(VictorSPX(DriveConstants.kRightFollowerB))
        selectProfileSlot(0, 0)
        selectedSensorPosition = 0
    }

    private val target = 1787.0 // 33 inches -> robot length

    private val tab = Shuffleboard.getTab("Drive Linear PID")

    val p: NetworkTableEntry = tab.add("P", 0).withWidget(BuiltInWidgets.kNumberSlider).entry
    val i: NetworkTableEntry = tab.add("I", 0).withWidget(BuiltInWidgets.kNumberSlider).entry
    val d: NetworkTableEntry = tab.add("D", 0).withWidget(BuiltInWidgets.kNumberSlider).entry

    val leftPos = tab.add("Left", 0).withWidget(BuiltInWidgets.kGraph).entry
    val rightPos = tab.add("Right", 0).withWidget(BuiltInWidgets.kGraph).entry

    var lastP = 0.0
    var lastI = 0.0
    var lastD = 0.0

    override fun robotPeriodic() {
        val newP = p.getDouble(0.0)
        if (!newP.epsilonEquals(lastP, 1E-9)) {
            lastP = newP
            leftMaster.config_kP(0, newP, 0)
            rightMaster.config_kP(0, newP, 0)
        }
        val newI = i.getDouble(0.0)
        if (!newI.epsilonEquals(lastI, 1E-9)) {
            lastI = newI
            leftMaster.config_kI(0, newI, 0)
            rightMaster.config_kI(0, newI, 0)
        }
        val newD = d.getDouble(0.0)
        if (!newD.epsilonEquals(lastD, 1E-9)) {
            lastD = newD
            leftMaster.config_kD(0, newD, 0)
            rightMaster.config_kD(0, newD, 0)
        }
        leftPos.setDouble(leftMaster.selectedSensorPosition.toDouble())
        rightPos.setDouble(rightMaster.selectedSensorPosition.toDouble())
    }

    override fun autonomousInit() {
        leftMaster.selectedSensorPosition = -0
        rightMaster.selectedSensorPosition = 0
    }

    override fun autonomousPeriodic() {
        leftMaster.set(ControlMode.Position, -target)
        rightMaster.set(ControlMode.Position, target)
    }

    override fun disabledInit() {
        leftMaster.disable()
        rightMaster.disable()
    }
}