package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.runOnce
import ca.warp7.frc2019.subsystems.superstructure.ManualOuttake

object OuttakeState {
    val kIdle = runOnce { }

    val OpenLoop = ManualOuttake
}