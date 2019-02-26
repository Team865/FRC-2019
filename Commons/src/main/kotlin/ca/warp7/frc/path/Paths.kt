@file:Suppress("unused")

package ca.warp7.frc.path

import ca.warp7.actionkt.Creator
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.geometry.degreesToRotation
import kotlin.math.pow

operator fun Path2D.get(t: Double): Path2DState {
    return Path2DState(t, px(t), py(t), vx(t), vy(t), ax(t), ay(t), jx(t), jy(t))
}

internal fun Double.checkBounds(): Double {
    if (this.isNaN() || this.isInfinite()) throw ArithmeticException("Path cannot use NaN or Infinity")
    if (this < 0 || this > 1) throw IndexOutOfBoundsException("Path cannot be interpolated beyond [0, 1]")
    return this
}

fun waypoint(x: Number, y: Number, angle: Number) =
        Pose2D(Translation2D(x.toDouble(), y.toDouble()), degreesToRotation(angle.toDouble()))

fun path(block: Creator<Pose2D>.() -> Unit): Path2D {
    val s = block(PathCreator()).toString()
    TODO(s)
}

val Path2DState.curvature get() = (vx * ay - ax * vy) / (vx * vx + vy * vy).pow(1.5)