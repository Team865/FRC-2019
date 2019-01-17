package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import koma.mat

object Localization : Subsystem() {
    var predictedState = mat[0, 0, 0, 0]
}