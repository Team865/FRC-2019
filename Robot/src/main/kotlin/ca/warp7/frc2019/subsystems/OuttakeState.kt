package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc.OpenLoopState

object OuttakeState {
    val kIdle = Outtake.runOnce { speed = 0.0 }

    val kOpenLoop = OpenLoopState { Outtake.speed = it }
}