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

fun quinticSplineFromPose(p0: Pose2D, p1: Pose2D): QuinticSegment2D {
    val scale = p0.translation.distanceTo(p1.translation)
    return QuinticSegment2D(
            x0 = p0.translation.x,
            x1 = p1.translation.x,
            dx0 = p0.rotation.cos * scale,
            dx1 = p1.rotation.cos * scale,
            ddx0 = 0.0,
            ddx1 = 0.0,
            y0 = p0.translation.y,
            y1 = p1.translation.y,
            dy0 = p0.rotation.sin * scale,
            dy1 = p1.rotation.sin * scale,
            ddy0 = 0.0,
            ddy1 = 0.0
    )
}

fun quinticSplinePathOf(vararg waypoints: Pose2D): List<QuinticSegment2D> =
        waypoints.asIterable().zipWithNext { p0: Pose2D, p1: Pose2D -> quinticSplineFromPose(p0, p1) }.optimized()

fun parameterizedPathOf(vararg waypoints: Pose2D) = quinticSplinePathOf(*waypoints).parameterizedWithArcLength()