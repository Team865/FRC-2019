package ca.warp7.frc.motion

import java.lang.Math.pow
import kotlin.math.atan
import kotlin.math.sqrt

data class Point2D(var x: Double, var y: Double) {
    // distance to origin
    val mag get() = sqrt(pow(x, 2.0) + pow(y, 2.0))

    // angle of point above the initial arm in radians
    val angle get() = atan(y / x)

    // sum of 2 points
    operator fun plus(p2: Point2D) = Point2D(x + p2.x, y + p2.y)

    // relative point
    operator fun minus(p2: Point2D) = Point2D(x - p2.x, y - p2.y)

    // sum of 2 points
    operator fun times(k: Double) = Point2D(k*x, k*y)

    // relative point
    operator fun div(k: Double) = Point2D(x/k, y/k)

    // distance between points
    operator fun rangeTo(p2: Point2D) = minus(p2).mag
}
