package ca.warp7.frc2019

import ca.warp7.actionkt.Action
import ca.warp7.frc.RobotControl
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.drive.DriveState

object Sandstorm : Action {

    private lateinit var auto: Action

    override fun start() {
        println("Robot State: Sandstorm")
        Drive.set(DriveState.kNeutralOutput)
        auto = Autonomous.mode
        auto.start()
    }

    override val shouldFinish: Boolean
        get() = auto.shouldFinish

    override fun update() {
        auto.update()
    }

    override fun stop() {
        auto.stop()
        RobotControl.set(MainLoop)
    }
}