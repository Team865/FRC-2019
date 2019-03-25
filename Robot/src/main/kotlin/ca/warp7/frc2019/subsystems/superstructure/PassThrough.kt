package ca.warp7.frc2019.subsystems.superstructure

import ca.warp7.actionkt.Action
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.constants.SuperstructureConstants
import ca.warp7.frc2019.subsystems.Conveyor
import ca.warp7.frc2019.subsystems.Outtake
import kotlin.math.withSign

object PassThrough : Action {

    var speed = 0.0
    var openOuttake = false
    var fastOuttake = true
    var stopOverrideOuttake = false

    override fun update() {
        Conveyor.speed = -speed * SuperstructureConstants.kConveyorSpeedScale
        if(!stopOverrideOuttake){
            Outtake.grabbing = openOuttake
        }
        Outtake.speed = when {
            speed.epsilonEquals(0.0, 0.1) -> 0.0
            fastOuttake -> SuperstructureConstants.kFastOuttakeSpeed
            else -> SuperstructureConstants.kNormalOuttakeSpeed.withSign(speed)
        }
    }

    override val shouldFinish: Boolean
        get() = false

    override fun stop() {
        Conveyor.speed = 0.0
        Outtake.speed = 0.0
    }
}