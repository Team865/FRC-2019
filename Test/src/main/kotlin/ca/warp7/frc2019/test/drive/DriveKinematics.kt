package ca.warp7.frc2019.test.drive

import ca.warp7.frc.control.config
import ca.warp7.frc.control.followedBy
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import kotlin.math.absoluteValue

@Suppress("unused", "MemberVisibilityCanBePrivate")
class DriveKinematics : TimedRobot() {

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

    private val tab = Shuffleboard.getTab("Drive Linear PID")

    private val dvLBuffer = mutableListOf<Int>()
    private val dvRBuffer = mutableListOf<Int>()
    private val dtBuffer = mutableListOf<Double>()

    private var previousTime = 0.0

    private var maxVelLeft = 0
    private var maxVelRight = 0
    private var maxAccLeft = 0.0
    private var maxAccRight = 0.0

    override fun robotPeriodic() {
        val lp = leftMaster.selectedSensorPosition
        val rp = rightMaster.selectedSensorPosition
        val lv = leftMaster.selectedSensorVelocity
        val rv = rightMaster.selectedSensorVelocity
        dvLBuffer.add(lv)
        dvRBuffer.add(rv)

        val newTime = Timer.getFPGATimestamp()
        val dt = newTime - previousTime
        previousTime = newTime

        if (dt.epsilonEquals(0.0, 1E-9)) return

        dtBuffer.add(dt)

        if (dvLBuffer.size > 5) dvLBuffer.removeAt(0)
        if (dvRBuffer.size > 5) dvRBuffer.removeAt(0)
        if (dtBuffer.size > 5) dtBuffer.removeAt(0)

        val la = dvLBuffer.sum() / dtBuffer.sum()
        val ra = dvRBuffer.sum() / dtBuffer.sum()

        if (lv.absoluteValue > maxVelLeft) maxVelLeft = lv.absoluteValue
        if (rv.absoluteValue > maxVelRight) maxVelRight = rv.absoluteValue
        if (la.absoluteValue > maxAccLeft) maxAccLeft = la.absoluteValue
        if (ra.absoluteValue > maxVelRight) maxAccRight = ra.absoluteValue

        tab.apply {
            add(leftMaster)
            add(rightMaster)
            add("left pos", lp)
            add("right pos", rp)
            add("left vel", lv)
            add("right vel", rv)
            add("left acc", la)
            add("right acc", ra)
            add("left max vel", maxVelLeft)
            add("right max vel", maxVelRight)
            add("left max acc", maxVelLeft)
            add("right max acc", maxVelRight)
        }
    }

    private val differentialDrive = DifferentialDrive(rightMaster, leftMaster)
    private val xboxController = XboxController(0)

    override fun teleopPeriodic() {
        if (xboxController.getBumper(GenericHID.Hand.kRight)) {
            differentialDrive.curvatureDrive(
                    xboxController.getY(GenericHID.Hand.kLeft),
                    xboxController.getX(GenericHID.Hand.kRight),
                    xboxController.getBumper(GenericHID.Hand.kLeft))
        } else {
            val speed = xboxController.getY(GenericHID.Hand.kLeft)
            differentialDrive.tankDrive(speed, speed)
        }
    }

    override fun disabledInit() {
        leftMaster.disable()
        rightMaster.disable()
    }
}