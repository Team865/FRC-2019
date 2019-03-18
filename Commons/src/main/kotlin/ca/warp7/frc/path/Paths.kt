@file:Suppress("unused")

package ca.warp7.frc.path

import ca.warp7.frc.geometry.*
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
        Pose2D(Translation2D(x.toDouble(), y.toDouble()), Rotation2D.fromDegrees(angle.toDouble()))

val Path2DState.curvature get() = (vx * ay - ax * vy) / (vx * vx + vy * vy).pow(1.5)

val Path2DState.position get() = Translation2D(px, py)

fun Path2DState.toPose() = Pose2D(position, Rotation2D(vx, vy).norm)