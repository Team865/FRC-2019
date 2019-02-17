package ca.warp7.frc2019.subsystems.superstructure

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Conveyor
import ca.warp7.frc2019.subsystems.Outtake

object PassThrough : Action {

    const val forward = 1.0
    const val reverse = -1.0

    var speed = 0.0
    var outtaking = false

    private val isLiftAtPositionForPassThrough get() = true // TODO use actual lift calculations

    override fun start() {
    }

    override fun update() {
        if (isLiftAtPositionForPassThrough) {
            Conveyor.speed = speed * 0.5
        }
        Outtake.grabbing = outtaking
        Outtake.speed = speed * 0.45
        Outtake.pushing = false
    }

    override val shouldFinish: Boolean
        get() = false

    override fun stop() {
        Conveyor.speed = 0.0
        Outtake.pushing = false
        Outtake.speed = 0.0
        Outtake.grabbing = false
    }
}