package ca.warp7.frckt

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

abstract class ShuffleSource {

    open fun onUpdateShuffleboard(container: ShuffleboardContainer) {}
}