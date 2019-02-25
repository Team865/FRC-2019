package ca.warp7.frc2019

import ca.warp7.actionkt.Action
import ca.warp7.frc.Controls
import ca.warp7.frc.runAutonomous

object Sandstorm : Action {

    private lateinit var autonomousAction: Action

    override fun start() {
        println("Robot State: Sandstorm")
        autonomousAction = runAutonomous(Autonomous.mode, timeout = 15.0)
    }

    override val shouldFinish: Boolean
        get() = autonomousAction.shouldFinish || Controls.robotDriver.leftXAxis > 0.8
                || Controls.robotOperator.leftXAxis > 0.8

    override fun stop() {
        autonomousAction.stop()
        MainLoop.start()
    }
}