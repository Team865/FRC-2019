package ca.warp7.frc2019

import ca.warp7.action.IAction
import ca.warp7.frc.ControlLoop
import ca.warp7.frckt.driver
import ca.warp7.frckt.operator
import ca.warp7.frckt.runRobotAutonomous
import ca.warp7.frckt.startControlLoop

object Sandstorm : ControlLoop {

    lateinit var autonomousAction: IAction

    override fun setup() {
        println("Robot State: Sandstorm")
        autonomousAction = runRobotAutonomous(Autonomous.mode, timeout = 15.0)
    }

    override fun periodic() {
        if (autonomousAction.shouldFinish() || driver.leftXAxis > 0.8 || operator.leftXAxis > 0.8) {
            autonomousAction.stop()
            startControlLoop(MainController)
        }
    }
}