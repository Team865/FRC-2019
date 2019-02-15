package ca.warp7.frc2019.test.lift

import ca.warp7.frc2019.constants.LiftConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import kotlin.math.absoluteValue
import kotlin.math.withSign

@Suppress("unused")
class LiftFeedforward : TimedRobot() {

    private lateinit var liftMaster: TalonSRX
    private lateinit var xboxController: XboxController

    private var feedforward = 0.05
    private var olr = 0.5

    private lateinit var olrEntry: NetworkTableEntry
    private lateinit var ffEntry: NetworkTableEntry

    private var leftPressed = false
    private var rightPressed = false

    override fun robotInit() {
        xboxController = XboxController(0)
        liftMaster = TalonSRX(LiftConstants.kMaster)
        VictorSPX(LiftConstants.kFollower).apply { inverted = true }.follow(liftMaster)
        val tab = Shuffleboard.getTab("Lift Feedforward")
        olrEntry = tab.add("ramp", 0.0)
                .withPosition(0, 0).withSize(10, 3).entry
        ffEntry = tab.add("feedforward", 0.0)
                .withPosition(0, 4).withSize(10, 3).entry
    }

    override fun disabledInit() {
        liftMaster.neutralOutput()
    }

    override fun teleopPeriodic() {
        if (xboxController.getBumper(GenericHID.Hand.kLeft) && !leftPressed) {
            feedforward -= 0.01
            leftPressed = true
        } else leftPressed = false
        if (xboxController.getBumper(GenericHID.Hand.kRight)) {
            feedforward += 0.01
            rightPressed = true
        } else rightPressed = false
        if (feedforward < 0.05) feedforward = 0.05
        var newOLR = olr
        if (xboxController.aButton) newOLR += 0.1
        if (xboxController.bButton) newOLR -= 0.1
        if (newOLR < 0) newOLR = 0.0
        if (newOLR != olr) {
            liftMaster.configOpenloopRamp(newOLR, 0)
            olr = newOLR
        }
        if (xboxController.yButton) {
            feedforward = 0.05
            olr = 0.0
            liftMaster.configOpenloopRamp(olr, 0)
        }
        val y = xboxController.getY(GenericHID.Hand.kLeft)
        liftMaster.set(ControlMode.PercentOutput,
                if (y.absoluteValue > 0.2) -(y - 0.2.withSign(y)) / 0.8 * 0.5 else 0.0,
                DemandType.ArbitraryFeedForward,
                feedforward)
    }

    override fun robotPeriodic() {
        olrEntry.setDouble(olr)
        ffEntry.setDouble(feedforward)
    }
}