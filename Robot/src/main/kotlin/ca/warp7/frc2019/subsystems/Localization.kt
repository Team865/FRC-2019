package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.math.Pose2d

object Localization : Subsystem() {
    var predictedPose = Pose2d(0.0, 0.0, 0.0)
}