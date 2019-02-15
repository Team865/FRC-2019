package ca.warp7.frc2019.test.low_goal_bot


import ca.warp7.frc2019.constants.ConveyorConstants
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.constants.IntakeConstants
import ca.warp7.frc2019.constants.OuttakeConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.drive.DifferentialDrive

class Lowbot : TimedRobot() {
    lateinit var controller: XboxController
    lateinit var leftConveyor: VictorSPX
    lateinit var rightConveyor: VictorSPX
    lateinit var intake: VictorSPX
    lateinit var differentialDrive: DifferentialDrive
    lateinit var leftOuttake: VictorSPX
    lateinit var rightOuttake: VictorSPX

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
        leftOuttake = VictorSPX(OuttakeConstants.kLeft)
        rightOuttake = VictorSPX(OuttakeConstants.kRight)
        differentialDrive = DifferentialDrive(rightMaster, leftMaster)
        leftConveyor = VictorSPX(ConveyorConstants.kLeft)
        rightConveyor = VictorSPX(ConveyorConstants.kRight)
        intake = VictorSPX(IntakeConstants.kVictor)
        controller = XboxController(0)
    }

    override fun disabledInit() {
        leftConveyor.neutralOutput()
        rightConveyor.neutralOutput()
        intake.neutralOutput()
        differentialDrive.stopMotor()

    }

    var xSpeed = 0.0
    var zRotation = 0.0
    override fun teleopPeriodic() {
        val left = controller.getTriggerAxis(GenericHID.Hand.kLeft)
        val right = controller.getTriggerAxis(GenericHID.Hand.kRight)
        var speed = 0.0
        if (left > 0.1) {
            speed = left * -1
        } else if (right > 0.1) {
            speed = right
        }
        speed *= 0.7

        rightConveyor.set(ControlMode.PercentOutput, speed / 2)
        leftConveyor.set(ControlMode.PercentOutput, speed / 2)
        intake.set(ControlMode.PercentOutput, speed)

        xSpeed += (controller.getY(GenericHID.Hand.kLeft) - xSpeed) / 6
        zRotation += (controller.getX(GenericHID.Hand.kRight) - zRotation) / 6

        differentialDrive.curvatureDrive(
                xSpeed,
                zRotation,
                controller.getBumper(GenericHID.Hand.kLeft)
        )
        val lt = controller.getTriggerAxis(GenericHID.Hand.kLeft)
        val rt = controller.getTriggerAxis(GenericHID.Hand.kRight)
        val outspeed = if (lt > rt) lt else -rt
        leftOuttake.set(ControlMode.PercentOutput, outspeed)
        rightOuttake.set(ControlMode.PercentOutput, outspeed)
    }
}