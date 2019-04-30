@file:Suppress("unused")

package ca.warp7.frc.path

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.geometry.norm
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt

operator fun Path2D.get(t: Double): Path2DState {
    return Path2DState(t, px(t), py(t), vx(t), vy(t), ax(t), ay(t), jx(t), jy(t))
}

internal fun Double.checkBounds(): Double {
    if (this.isNaN() || this.isInfinite()) throw ArithmeticException("Path cannot use NaN or Infinity")
    if (this < 0 || this > 1) throw IndexOutOfBoundsException("Path cannot be interpolated beyond [0, 1]")
    return this
}

val Path2DState.curvature get() = (vx * ay - ax * vy) / (vx * vx + vy * vy).pow(1.5)

val Path2DState.point get() = Translation2D(px, py)

val Path2DState.heading get() = Rotation2D(vx, vy).norm

fun Path2DState.toPose() = Pose2D(point, heading)

val Path2DState.dCurvature: Double
    get() {
        val dx2dy2 = vx.pow(2) + vy.pow(2)
        val num = (vx * jy - jx * vy) * dx2dy2 - 3.0 * (vx * ay - ax * vy) * (vx * ax + vy * ay)
        return num / (dx2dy2 * dx2dy2 * sqrt(dx2dy2))
    }

val Path2DState.dk_ds: Double get() = dCurvature / hypot(vx, vy)

val Path2DState.dCurvature2: Double
    get() {
        val dx2dy2 = vx.pow(2) + vy.pow(2)
        val num = (vx * jy - jx * vy) * dx2dy2 - 3.0 * (vx * ay - ax * vy) * (vx * ax + vy * ay)
        return num.pow(2) / dx2dy2.pow(5)
    }


fun quinticSplineFromPose(p0: Pose2D, p1: Pose2D): QuinticSegment2D {
    val scale = p0.translation.distanceTo(p1.translation) * 1.2
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

fun quinticSplinesOf(vararg waypoints: Pose2D, optimizePath: Boolean = false): List<QuinticSegment2D> {
    val path = mutableListOf<QuinticSegment2D>()
    for (i in 0 until waypoints.size - 1) path.add(quinticSplineFromPose(waypoints[i], waypoints[i + 1]))
    if (optimizePath) return path.optimized()
    return path
}

fun parameterizedSplinesOf(vararg waypoints: Pose2D) = quinticSplinesOf(*waypoints).parameterized()