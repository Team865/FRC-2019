package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.path.waypoint

class SetRobotState(val xInFeet: Number, val yInFeet: Number, val angleInDegrees: Number) : Action {
    override fun start() {
        DriveMotionPlanner.robotState = waypoint(xInFeet, yInFeet, angleInDegrees)
    }
}