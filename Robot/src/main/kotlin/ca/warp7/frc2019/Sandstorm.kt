package ca.warp7.frc2019

import ca.warp7.actionkt.Action
import ca.warp7.frc.Controls

object Sandstorm : Action {

    private lateinit var auto: Action

    override fun start() {
        println("Robot State: Sandstorm")
        auto = Autonomous.mode()
    }

    override val shouldFinish: Boolean
        get() = auto.shouldFinish || Controls.robotDriver.leftXAxis > 0.8
                || Controls.robotOperator.leftXAxis > 0.8

    override fun stop() {
        auto.stop()
        MainLoop.start()
    }
}