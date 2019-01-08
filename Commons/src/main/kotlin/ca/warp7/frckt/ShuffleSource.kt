package ca.warp7.frckt

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

interface ShuffleSource {

    open fun onUpdateShuffleboard(container: ShuffleboardContainer) {}
}