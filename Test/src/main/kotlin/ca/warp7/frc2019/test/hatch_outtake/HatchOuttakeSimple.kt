package ca.warp7.frc2019.test.hatch_outtake

import ca.warp7.frc2019.constants.HatchConstants
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController

@Suppress("unused")
class HatchOuttakeSimple : TimedRobot() {
    lateinit var solenoid: Solenoid
    lateinit var controller: XboxController
    override fun robotInit() {
        solenoid = Solenoid(HatchConstants.kSolenoid)
        controller = XboxController(0 )
    }

    override fun teleopPeriodic() {
        solenoid.set(controller.aButton)
    }
}
