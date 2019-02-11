package ca.warp7.frc2019.test.drive_curvature

import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.drive.DifferentialDrive

@Suppress("unused")
class DriveTest : TimedRobot() {

    lateinit var differentialDrive: DifferentialDrive
    lateinit var xboxController: XboxController

    override fun robotInit() {
        val leftMaster = WPI_TalonSRX(DriveConstants.kLeftMaster).also {
            it.configOpenloopRamp(1.0)
            VictorSPX(DriveConstants.kLeftFollowerA).follow(it)
            VictorSPX(DriveConstants.kLeftFollowerB).follow(it)
        }

        val rightMaster = WPI_TalonSRX(DriveConstants.kRightMaster).also {
            it.configOpenloopRamp(1.0)
            VictorSPX(DriveConstants.kRightFollowerA).follow(it)
            VictorSPX(DriveConstants.kRightFollowerB).follow(it)
        }

        differentialDrive = DifferentialDrive(leftMaster, rightMaster)
        xboxController = XboxController(0)
    }

    override fun disabledInit() {
        differentialDrive.stopMotor()
    }

    override fun teleopPeriodic() {
        differentialDrive.curvatureDrive(
                xboxController.getY(GenericHID.Hand.kLeft),
                xboxController.getX(GenericHID.Hand.kRight),
                xboxController.getBumper(GenericHID.Hand.kLeft)
        )
    }
}