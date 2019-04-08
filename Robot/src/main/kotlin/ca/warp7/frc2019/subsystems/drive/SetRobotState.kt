package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.path.waypoint

class SetRobotState(val state: Pose2D) : Action {

    constructor(xInFeet: Number, yInFeet: Number, angleInDegrees: Number) :
            this(waypoint(xInFeet, yInFeet, angleInDegrees))

    override fun start() {
        DriveMotionPlanner.robotState = state
    }
}