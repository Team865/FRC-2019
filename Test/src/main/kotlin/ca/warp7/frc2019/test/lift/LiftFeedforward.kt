package ca.warp7.frc2019.test.lift

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
class LiftFeedforward : TimedRobot() {

    lateinit var liftMaster: TalonSRX
    lateinit var xboxController: XboxController

    var feedforward = 0.05
    var olr = 0.0

    override fun robotInit() {
        xboxController = XboxController(0)
        liftMaster = TalonSRX(LiftConstants.kMaster)
        VictorSPX(LiftConstants.kFollower).apply { inverted = true }.follow(liftMaster)
    }

    override fun disabledInit() {
        liftMaster.neutralOutput()
    }

    override fun teleopPeriodic() {
        if (xboxController.getBumper(GenericHID.Hand.kLeft)) feedforward -= 0.01
        if (xboxController.getBumper(GenericHID.Hand.kRight)) feedforward += 0.01
        var newOLR = olr
        if (xboxController.aButton) newOLR += 0.1
        if (xboxController.bButton) newOLR -= 0.1
        if (newOLR < 0) newOLR = 0.0
        if (newOLR != olr) {
            liftMaster.configOpenloopRamp(newOLR, 0)
            olr = newOLR
        }
        val y = xboxController.getY(GenericHID.Hand.kLeft)
        liftMaster.set(ControlMode.PercentOutput,
                if (y.absoluteValue > 0.2) -(y - 0.2.withSign(y)) / 0.8 * 0.5 else 0.0,
                DemandType.ArbitraryFeedForward,
                feedforward)
    }
}