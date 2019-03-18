package ca.warp7.frc2019.test.drive.velocity_control

import ca.warp7.frc.*
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure
import edu.wpi.first.wpilibj.TimedRobot

@Suppress("unused")
class DriveVelocityControl : TimedRobot() {

    /**
     * Initializes the robot by setting the state of subsystems
     * directly or transitively and activating the controllers.
     * Comment out the controller lines to disable them
     */
    override fun robotInit() {
        println("Hello me is robit!")
        RobotControl.set { mode = ControllerMode.DriverOnly }
        Drive.set { motionPlanner.set { } }
        Infrastructure.set { }
    }

    /**
     * Runs a periodic loop that collects inputs, updates controller loop,
     * processes subsystem states, send output signals, and send telemetry
     */
    override fun robotPeriodic() = runPeriodicLoop()

    /**
     * Disables the robot by disabling each subsystem and not calling
     * output methods.
     */
    override fun disabledInit() = disableRobot()

    /*
    =====================================================
    Starts various modes of the robot using control loops
    =====================================================
     */

    override fun autonomousInit() = RobotControl.enable(VelocityControlLoop)
    override fun teleopInit() = disableRobot()
    override fun testInit() = disableRobot()

    /*
    =====================================================
    The following periodic functions are not used because
    they are all handled by the `robotPeriodic` function
    =====================================================
     */

    override fun disabledPeriodic() = Unit
    override fun autonomousPeriodic() = Unit
    override fun teleopPeriodic() = Unit
    override fun testPeriodic() = Unit
}