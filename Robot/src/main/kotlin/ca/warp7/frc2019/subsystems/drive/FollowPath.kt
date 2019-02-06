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
        var i=0
        queue {!"generate trajectory"
            for (waypoint in path.waypoints) {
                !i.toString()
                val relativeAngle = previousAngle - (previousPoint - waypoint.point).angle

                !"turn angle"
                +TurnAngle
                angle = relativeAngle
                !"turn angle end"

                !"drive distance"
                +DriveDistance
                distance = (waypoint.point - previousPoint).mag
                !"drive distance end"

                previousPoint = waypoint.point
                previousAngle = angle

                i++
            }
        }
    }
}
