package ca.warp7.frc.trajectory

import ca.warp7.frc.geometry.State

class Trajectory<T : State<T>>(val points: List<T>) : StateListView<T>