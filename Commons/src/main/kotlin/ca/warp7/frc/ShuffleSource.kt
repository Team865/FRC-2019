package ca.warp7.frc

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

interface ShuffleSource {

    fun onUpdateShuffleboard(container: ShuffleboardContainer) {}
}