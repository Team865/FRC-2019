package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc.OpenLoopState

object OuttakeState {
    val kIdle = runOnce { }

    val OpenLoop = OpenLoopState { Outtake.speed = it }
}