package ca.warp7.frc2019

import ca.warp7.frc.*
import ca.warp7.frc2019.subsystems.*
import ca.warp7.frc2019.subsystems.lift.MainDisplay
import edu.wpi.first.wpilibj.TimedRobot

class Astro : TimedRobot() {

    /**
     * Initializes the robot by setting the state of subsystems
     * directly or transitively and activating the controllers.
     * Comment out the controller lines to disable them
     */
    override fun robotInit() {
        println("Hello me is robit!")
        RobotControl.set { mode = ControllerMode.DriverAndOperator }
        MainDisplay.set { }
        Drive.set { motionPlanner.set { } }
        Infrastructure.set { }
        Limelight.set { isDriver = false }
        Conveyor.set { speed = 0.0 }
        Outtake.set {
            speed = 0.0
            grabbing = true
            pushing = false
        }
        Intake.set { extended = false }
        Lift.set { }
        Superstructure.set { }
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

    override fun autonomousInit() = RobotControl.set(Sandstorm)
    override fun teleopInit() = RobotControl.set(MainLoop)
    override fun testInit() = RobotControl.set(MainLoop)

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

    /**
     * The main function that is executed from the `Main-Class` of the jar file.
     * It calls on RobotBase to initialize system hardware and start the main
     * loop that calls the other functions
     */
    @Suppress("UnusedMainParameter")
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = TimedRobot.startRobot { Astro() }
    }
}