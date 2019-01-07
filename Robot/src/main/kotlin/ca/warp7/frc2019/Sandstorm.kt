package ca.warp7.frc2019

import ca.warp7.frckt.*

object Sandstorm : ControlLoop {

    lateinit var autonomousAction: Action

    override fun setup() {
        println("Robot State: Sandstorm")
        autonomousAction = runAutonomous(Autonomous.mode, timeout = 15.0)
    }

    override fun periodic() {
        if (autonomousAction.shouldFinish() || driver.leftXAxis > 0.8 || operator.leftXAxis > 0.8) {
            autonomousAction.stop()
            setControlLoop(MainController)
        }
    }
}