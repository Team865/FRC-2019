package ca.warp7.frc2019

import ca.warp7.actionkt.runOnce
import ca.warp7.frc.disableRobot
import ca.warp7.frc.runPeriodicLoop
import ca.warp7.frc.start
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.subsystems.*
import edu.wpi.first.wpilibj.TimedRobot

class Robot : TimedRobot(ControlConstants.kLoopPeriod) {

    /**
     * Initializes the robot by setting the state of subsystems
     * directly or transitively and activating the controllers.
     * Comment out the controller lines to disable them
     */
    override fun robotInit() {
        println("Hello me is robit!")
        Drive.set(DriveState.kNeutralMotionState)
        Infrastructure.set(Infrastructure.runOnce { startCompressor = true })
        Climber.set(Climber.runOnce { climbing = false })
        Intake.set(IntakeState.kUp)
        Conveyor.set(ConveyorState.kIdle)
        Lift.set(LiftState.kIdle)
        Outtake.set(OuttakeState.kIdle)
        Hatch.set(Hatch.runOnce { pushing = false })
        Superstructure.set(SuperstructureState.kStartingConfiguration)
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

    override fun autonomousInit() = SandstormLoop.start()
    override fun teleopInit() = MainLoop.start()
    override fun testInit() = MainLoop.start()

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
        fun main(args: Array<String>) = TimedRobot.startRobot { Robot() }
    }
}