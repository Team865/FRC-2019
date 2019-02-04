package ca.warp7.frc.Motion

import java.lang.Math.pow
import java.util.function.Function
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.atan
import kotlin.math.sqrt

data class Point2D(var x: Double, var y: Double) {
    // distance to origin
    val mag get() = sqrt(pow(x, 2.0) + pow(y, 2.0))

    // angle of point above the initial arm in radians
    val angle get() = atan(y / x)

    // sum of 2 points
    operator fun plus(b: Point2D) = Point2D(x + b.x, y + b.y)

    // relative point
    operator fun minus(b: Point2D) = Point2D(x - b.x, y - b.y)

    // scale up point
    operator fun times(b: Double) = Point2D(x * b, y * b)

    // scale down point
    operator fun div(b: Double) = Point2D(x / b, y / b)

    // distance to another point
    operator fun rangeTo(b: Point2D) = minus(b).mag
}
