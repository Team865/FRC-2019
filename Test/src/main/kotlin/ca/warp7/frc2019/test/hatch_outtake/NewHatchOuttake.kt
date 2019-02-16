package ca.warp7.frc2019.test.hatch_outtake

import ca.warp7.frc.lazySolenoid
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController

class NewHatchOuttake : TimedRobot() {
    lateinit var arms : Solenoid
    lateinit var pusher : Solenoid
    lateinit var controller : XboxController
    override fun robotInit() {
        arms = lazySolenoid(0)//TODO
        pusher = lazySolenoid(0)//TODO
        controller = XboxController(0)
    }

    override fun teleopPeriodic() {
        arms.set(controller.yButton)
        pusher.set(controller.yButton)
    }
}


