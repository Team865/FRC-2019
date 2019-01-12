package ca.warp7.frc2019

import ca.warp7.frc.Controls
import ca.warp7.frc.disableRobot
import ca.warp7.frc.runMainLoop
import ca.warp7.frc.setControlLoop
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.DriveState
import ca.warp7.frc2019.subsystems.Electrical
import ca.warp7.frc2019.subsystems.ElectricalState
import edu.wpi.first.wpilibj.TimedRobot

class Robot2019 : TimedRobot(ControlConstants.kLoopPeriod) {

    companion object {

        /**
         * The main function that is executed from the `Main-Class` of the jar file.
         * It calls on TimedRobot to initialize system hardware and start the main
         * loop that calls the other functions
         */
        @JvmStatic
        fun main(args: Array<String>) = TimedRobot.startRobot { Robot2019() }
    }

    /**
     * Initializes the robot by registering input devices, then setting all the
     * subsystem objects to their idle state. Finally initialize the runtime
     */
    override fun robotInit() {
        println("Hello me is robit!")

        Electrical.set(ElectricalState.Idle)
        Drive.set(DriveState.Brake)

        Controls.driver.enabled = true
        Controls.operator.enabled = true
    }

    /**
     * Disables the robot by disabling each subsystem and not calling output methods.
     * Stops the autonomous routine if there is one
     */
    override fun disabledInit() = disableRobot()

    /**
     * Starts the autonomous mode by providing a control loop
     */
    override fun autonomousInit() = setControlLoop(Sandstorm)

    /**
     * Starts the teleop mode by providing a control loop.
     * Stops the autonomous routine if there is one
     */
    override fun teleopInit() = setControlLoop(MainControl)

    /**
     * Starts the test mode by providing a potentially different control loop
     */
    override fun testInit() = setControlLoop(TestControl)

    /**
     * Runs a periodic loop that collects inputs, update the autonomous
     * routine and controller loop, process subsystem states, send output
     * signals, and send telemetry data
     */
    override fun robotPeriodic() = runMainLoop()

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