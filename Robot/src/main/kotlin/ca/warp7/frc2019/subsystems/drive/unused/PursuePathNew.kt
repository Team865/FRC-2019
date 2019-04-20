package ca.warp7.frc2019.subsystems.drive.unused

import ca.warp7.actionkt.Action
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.geometry.radians
import ca.warp7.frc.trajectory.IndexedTrajectory
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.tan

class PursuePathNew(path: IndexedTrajectory<Pose2D, Pose2D>) : Action {
    //var path = Path(emptyList())
    var lookaheadDistance = 0.2
    var lookaheadGain = 0.05
    val points = path.points

    override fun update() {
        val x = DriveMotionPlanner.robotState.translation.x
        val y = DriveMotionPlanner.robotState.translation.y
        val yaw = DriveMotionPlanner.robotState.rotation.radians
        val p = Translation2D(x, y)

        var i = points.map { (it.translation - p).mag }.let { it.indexOf(it.min()) }
        var distance = (points[i].translation - p).mag

        while (distance <= lookaheadDistance && i < points.size) {
            i++
            distance += (points[i].translation - points[i - 1].translation).mag
        }

        val t = points[i].translation

        val slope = tan(yaw + PI / 2)

        val r = Translation2D(
                ((1 - slope * slope) * t.x + 2 * (slope * t.y - slope * (p.y - slope * p.x))) / (slope * slope + 1), //TODO suspicious slope
                ((slope * slope - 1) * t.y + 2 * (slope * t.x + (p.y - slope * p.x))) / (slope * slope + 1)
        )

        val linePT = Pair(-(t.x - p.x) / (t.y - p.y), (p.y + t.y + (t.x - p.x) * (p.x + t.x) / (t.y - p.y)) / 2)
        val linePR = Pair(-(r.x - p.x) / (r.y - p.y), (p.y + r.y + (r.x - p.x) * (p.x + r.x) / (r.y - p.y)) / 2)


        val center = Translation2D(
                -(linePT.second - linePR.second) / (linePT.first - linePR.first),
                -linePT.first * (linePT.second - linePR.second) / (linePT.first - linePR.first) + linePT.second
        )

        val radius = (center - p).mag

        val curvature = 1 / radius
        val arcLength = 2 * radius * asin((t - p).mag / (2 * radius))//TODO make this account for if the arc length is more than 0.5x the circumference
        val endVel = DriveConstants.kMaxVelocity / (1 + curvature * DriveConstants.kTurningDiameter / 2)

        println("$arcLength, $endVel")
    }
}