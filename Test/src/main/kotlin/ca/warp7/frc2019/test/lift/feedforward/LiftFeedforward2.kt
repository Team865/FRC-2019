package ca.warp7.frc2019.test.lift.feedforward

import ca.warp7.frc.*
import ca.warp7.frc2019.subsystems.Conveyor
import ca.warp7.frc2019.subsystems.Outtake
import edu.wpi.first.wpilibj.TimedRobot

@Suppress("unused")
class LiftFeedforward2 : TimedRobot() {

    /**
     * Initializes the robot by setting the state of subsystems
     * directly or transitively and activating the controllers.
     * Comment out the controller lines to disable them
     */
    override fun robotInit() {
        println("Hello me is robit!")
        RobotControl.set { mode = ControllerMode.DriverOnly }
        LiftSubsystem.set { }
        Outtake.set { }
        Conveyor.set { }
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
    override fun disabledInit() = disableRobot()

    /*
    =====================================================
    Starts various modes of the robot using control loops
    =====================================================
     */

    override fun autonomousInit() = Unit
    override fun teleopInit() = RobotControl.set(LiftFeedforwardLoop)
    override fun testInit() = RobotControl.set(LiftFeedforwardLoop)

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