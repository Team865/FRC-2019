package ca.warp7.frc.motion

open class Path(val waypoints: List<Waypoint>) {
    operator fun plus(b: Point2D): Path {
        return Path(waypoints.map { Waypoint(it.point + b, it.angle, it.mag) })
    }
    operator fun minus(b: Point2D): Path {
        return Path(waypoints.map { Waypoint(it.point - b, it.angle, it.mag) })
    }
}
