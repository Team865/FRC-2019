package ca.warp7.frc2019.test.lift_outtake_simple

import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.constants.OuttakeConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import kotlin.math.absoluteValue
import kotlin.math.withSign

@Suppress("unused")
class LiftOuttakeTest : TimedRobot() {

    lateinit var liftMaster: TalonSRX
    lateinit var xboxController: XboxController
    lateinit var leftOuttake: VictorSPX
    lateinit var rightOuttake: VictorSPX

    override fun robotInit() {
        xboxController = XboxController(0)
        liftMaster = TalonSRX(LiftConstants.kMaster)
        VictorSPX(LiftConstants.kFollower).follow(liftMaster)
        leftOuttake = VictorSPX(OuttakeConstants.kLeft)
        rightOuttake = VictorSPX(OuttakeConstants.kRight)
    }

    override fun disabledInit() {
        liftMaster.neutralOutput()
    }

    override fun teleopPeriodic() {
        val y = xboxController.y
        if (y.absoluteValue > 0.1) liftMaster.set(ControlMode.PercentOutput, (y - 0.1.withSign(y)) / 0.9)
        val lt = xboxController.getTriggerAxis(GenericHID.Hand.kLeft)
        val rt = xboxController.getTriggerAxis(GenericHID.Hand.kRight)
        val speed = if (lt > rt) lt else -rt
        leftOuttake.set(ControlMode.PercentOutput, speed)
        rightOuttake.set(ControlMode.PercentOutput, speed)
    }
}