package ca.warp7.frc.motion

open class Path(val waypoints: List<Waypoint>) {
    operator fun plus(p: Point2D): Path {
        return Path(waypoints.map { Waypoint(it.point + p, it.angle, it.mag) })
    }

    operator fun minus(p: Point2D): Path {
        return Path(waypoints.map { Waypoint(it.point - p, it.angle, it.mag) })
    }

    fun closestPoint(p: Point2D): Int{
        val delta = waypoints.map{it.point..p}
        return delta.indexOf(delta.min())
    }
}
