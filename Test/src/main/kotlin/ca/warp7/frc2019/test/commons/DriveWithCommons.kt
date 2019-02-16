package ca.warp7.frc2019.test.commons

import ca.warp7.frc.*
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.DriveState
import ca.warp7.frc2019.subsystems.Infrastructure
import edu.wpi.first.wpilibj.TimedRobot

@Suppress("unused")
class DriveWithCommons : TimedRobot() {

    /**
     * Initializes the robot by setting the state of subsystems
     * directly or transitively and activating the controllers.
     * Comment out the controller lines to disable them
     */
    override fun robotInit() {
        println("Hello me is robit!")
        setControllerMode(ControllerMode.DriverOnly)
        Drive.set(DriveState.kNeutralOutput)
        Infrastructure.set { startCompressor = true }
    }

    /**
     * Runs a periodic loop that collects inputs, update the autonomous
     * routine and controller loop, process subsystem states, send output
     * signals, and send telemetry data
     */
    override fun robotPeriodic() = runPeriodicLoop()

    /**
     * Disables the robot by disabling each subsystem and not calling
     * output methods. Stops the autonomous routine
     */
    override fun disabledInit() = println("Robot Disabled ------12345-------").also { disableRobot() }

    /*
    =====================================================
    Starts various modes of the robot using control loops
    =====================================================
     */

    override fun autonomousInit() = Unit
    override fun teleopInit() = DriveWithCommonsLoop.start().also { println("Robot Enabled ------12345-------") }
    override fun testInit() = DriveWithCommonsLoop.start()

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