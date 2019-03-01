package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.motion.Path
import ca.warp7.frc.motion.Point2D
import ca.warp7.frc2019.constants.DriveConstants
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.tan

object PursuePath : Action {
    var path = Path(emptyList())
    var lookaheadDistance = 0.2
    var lookaheadGain = 0.05

    override fun update() {
        val x = DriveMotionPlanner.motionState.x
        val y = DriveMotionPlanner.motionState.y
        val yaw = DriveMotionPlanner.motionState.yaw
        val p = Point2D(x, y)

        var i = path.closestPoint(p)
        var distance = path.waypoints[i].point..p

        while (distance <= lookaheadDistance && i < path.waypoints.size) {
            i++
            distance += path.waypoints[i].point..path.waypoints[i - 1].point
        }

        val t = path.waypoints[i].point

        val slope = tan(yaw + PI / 2)

        val r = Point2D(
                ((1 - slope * slope) * t.x + 2 * (slope * t.y - slope * (p.y - slope * p.x))) / (slope * slope + 1), //TODO suspicious slope
                ((slope * slope - 1) * t.y + 2 * (slope * t.x + (p.y - slope * p.x))) / (slope * slope + 1)
        )

        val linePT = Pair(-(t.x - p.x) / (t.y - p.y), (p.y + t.y + (t.x - p.x) * (p.x + t.x) / (t.y - p.y)) / 2)
        val linePR = Pair(-(r.x - p.x) / (r.y - p.y), (p.y + r.y + (r.x - p.x) * (p.x + r.x) / (r.y - p.y)) / 2)


        val circumcenter = Point2D(
                -(linePT.second - linePR.second) / (linePT.first - linePR.first),
                -linePT.first * (linePT.second - linePR.second) / (linePT.first - linePR.first) + linePT.second
        )

        val radius = p..circumcenter

        val curvature = 1 / radius
        val arcLength = 2 * radius * asin((p..t) / (2 * radius))//TODO make this account for if the arc length is more than 0.5x the circumference
        val endVel = DriveConstants.kMaxVelocity / (1 + curvature * DriveConstants.kTurningDiameter / 2)
    }
}