package ca.warp7.frc2019.test.lift_simple

import ca.warp7.frc2019.constants.LiftConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import kotlin.math.absoluteValue
import kotlin.math.withSign

@Suppress("unused")
class LiftSimple : TimedRobot() {

    lateinit var liftMaster: TalonSRX
    lateinit var xboxController: XboxController

    override fun robotInit() {
        xboxController = XboxController(0)
        liftMaster = TalonSRX(LiftConstants.kMaster)
        liftMaster.configOpenloopRamp(0.8)
        VictorSPX(LiftConstants.kFollower).apply { inverted = true }.follow(liftMaster)
    }

    override fun disabledInit() {
        liftMaster.neutralOutput()
    }

    override fun teleopPeriodic() {
        val y = xboxController.getY(GenericHID.Hand.kLeft)
        liftMaster.set(ControlMode.PercentOutput,
                if (y.absoluteValue > 0.2) -(y - 0.2.withSign(y)) / 0.8 * 0.5 else 0.0,
                DemandType.ArbitraryFeedForward,
                0.05)
    }
}