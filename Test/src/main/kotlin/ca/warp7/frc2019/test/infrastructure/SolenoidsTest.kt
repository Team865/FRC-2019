package ca.warp7.frc2019.test.infrastructure

import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController

@Suppress("unused")
class SolenoidsTest : TimedRobot() {
    val xboxController = XboxController(0)
    val s0 = Solenoid(0)
    val s1 = Solenoid(1)
    val s2 = Solenoid(2)
    val s3 = Solenoid(4)

    override fun teleopInit() {
        s0.set(xboxController.aButton)
        s1.set(xboxController.bButton)
        s2.set(xboxController.xButton)
        s3.set(xboxController.yButton)
    }
}