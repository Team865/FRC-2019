package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.*
import ca.warp7.frc.motion.Path
import ca.warp7.frc.motion.Point2D
import ca.warp7.frc2019.subsystems.drive.DriveDistance.distance
import ca.warp7.frc2019.subsystems.drive.TurnAngle.angle

object FollowPath : Action {
    lateinit var path: Path

    var startPos = Point2D(0.0, 0.0)
    var startAngle = 0.0

    override fun start() {
        var previousPoint = startPos
        var previousAngle = startAngle
        queue {
            for (waypoint in path.waypoints) {
                val relativeAngle = previousAngle - (previousPoint - waypoint.point).angle

                +TurnAngle
                angle = relativeAngle

                +DriveDistance
                distance = (waypoint.point - previousPoint).mag

                previousPoint = waypoint.point
                previousAngle = angle
            }
        }
    }
}
