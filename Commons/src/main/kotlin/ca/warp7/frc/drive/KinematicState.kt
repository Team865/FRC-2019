package ca.warp7.frc.drive

data class KinematicState(
        val velocity: ChassisState,
        val acceleration: ChassisState
)