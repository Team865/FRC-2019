package ca.warp7.frc

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

interface ShuffleboardUpdater {

    fun onUpdateShuffleboard(container: ShuffleboardContainer) {}
}