package ca.warp7.frc2019.subsystems.superstructure

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.SuperstructureConstants
import ca.warp7.frc2019.subsystems.Conveyor
import ca.warp7.frc2019.subsystems.Outtake
import kotlin.math.withSign

object PassThrough : Action {

    var speed = 0.0
    var outtaking = false

    override fun update() {
        Conveyor.speed = -speed * SuperstructureConstants.kConveyorSpeedScale
        Outtake.grabbing = outtaking
        Outtake.speed = SuperstructureConstants.kOuttakeSpeed.withSign(speed)
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