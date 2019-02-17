package ca.warp7.frc2019.test.infrastructure

import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.TimedRobot

class CompressorTest : TimedRobot() {
    override fun robotInit() {
        Solenoid(0)
    }
}