package ca.warp7.frc2019.test.limelight

import ca.warp7.actionkt.action
import ca.warp7.actionkt.periodic
import ca.warp7.frc.*
import ca.warp7.frc2019.subsystems.Limelight
import edu.wpi.first.wpilibj.TimedRobot

@Suppress("unused")
class LimelightTest : TimedRobot() {
    override fun robotInit() {
        println("Hello me is robit!")
        RobotControl.set { mode = ControllerMode.DriverOnly }
        Limelight.set(action {})
    }

    override fun robotPeriodic() = runPeriodicLoop()

    override fun disabledInit() = disableRobot()

    override fun teleopInit() = RobotControl.enable(periodic {
        withDriver {
            if (xButton==ControllerState.Pressed){
                Limelight.isDriver=!Limelight.isDriver
            }
            println(Limelight.isDriver)
            println(xButton)
            println()
        }
    })
}