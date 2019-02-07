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

    private val queue = queue {
        !"generate trajectory"

        var previousPoint = startPos
        var previousAngle = startAngle
        for ((i, waypoint) in path.waypoints.withIndex()) {
            val relativeAngle = previousAngle - (previousPoint - waypoint.point).angle
            val relativeDistance = (waypoint.point - previousPoint).mag

            !"turn angle"
            +TurnAngle
            angle = relativeAngle

            !"drive distance"
            +DriveDistance
            distance = relativeDistance

            previousPoint = waypoint.point
            previousAngle = angle

        }
    }

    override fun start() = queue.start()

    override fun update() = queue.update()

    override val shouldFinish: Boolean get() = queue.shouldFinish


}
