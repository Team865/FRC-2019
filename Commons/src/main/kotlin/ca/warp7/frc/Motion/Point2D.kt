package ca.warp7.frc.Motion

import java.lang.Math.pow
import kotlin.math.atan
import kotlin.math.sqrt

data class Point2D(var x: Double, var y: Double) {
    fun mag(): Double {
        // distance to origin
        return sqrt(pow(x, 2.0) + pow(y, 2.0))
    }

    fun angle(): Double {
        // angle of point above the initial arm in radians
        return atan(y / x)
    }

    operator fun plus(b: Point2D): Point2D {
        //
        return Point2D(x + b.x, y + b.y)
    }

    operator fun minus(b: Point2D): Point2D {
        // Relative point
        return Point2D(x - b.x, y - b.y)
    }

    operator fun times(b: Double): Point2D {
        // Scale up point
        return Point2D(x * b, y * b)
    }

    operator fun div(b: Double): Point2D {
        // Scale down point
        return Point2D(x / b, y / b)
    }

    operator fun rangeTo(b:Point2D):Double{
        // Distance to another point
        return minus(b).mag()
    }
}
