package ca.warp7.frc2019.helper

import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.trajectory.TrajectoryState

val TrajectoryState.velocity get() = ChassisState(v, w)
val TrajectoryState.acceleration get() = ChassisState(dv, dw)