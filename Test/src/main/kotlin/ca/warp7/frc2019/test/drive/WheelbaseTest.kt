package ca.warp7.frc2019.test.drive

import ca.warp7.actionkt.periodic
import ca.warp7.frc.*
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner
import edu.wpi.first.wpilibj.TimedRobot

class WheelbaseTest : TimedRobot() {
    override fun robotInit() {
        RobotControl.set { mode = ControllerMode.DriverOnly }
        Drive.set { }
        Infrastructure.set { }
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

    override fun autonomousInit() = RobotControl.set(periodic {
        Drive.leftDemand = 0.8
        Drive.rightDemand = -0.8
        val wheelbase = DriveMotionPlanner.model
                .solveWheelbase(DriveMotionPlanner.wheelVelocity, Infrastructure.yawRate)
        RobotControl.put("wheelbase", wheelbase)
    })

    override fun teleopInit() = Unit
    override fun testInit() = Unit

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