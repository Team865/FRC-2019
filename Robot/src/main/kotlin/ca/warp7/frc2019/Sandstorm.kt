package ca.warp7.frc2019

import ca.warp7.frc.ControlLoop
import ca.warp7.frckt.*

object Sandstorm : ControlLoop {

    lateinit var autonomousAction: Action

    override fun setup() {
        println("Robot State: Sandstorm")
        autonomousAction = runRobotAutonomous(Autonomous.mode, timeout = 15.0).ktAction
    }

    override fun periodic() {
        if (autonomousAction.shouldFinish() || driver.leftXAxis > 0.8 || operator.leftXAxis > 0.8) {
            autonomousAction.stop()
            startControlLoop(MainController)
        }
    }
}