@file:Suppress("unused")

package ca.warp7.frc.path

import ca.warp7.frc.geometry.ArcPose2D
import ca.warp7.frc.geometry.Pose2D

operator fun Path2D.get(t: Double): Path2DState {
    return Path2DState(t, px(t), py(t), vx(t), vy(t), ax(t), ay(t), jx(t), jy(t))
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

fun parameterizedSplinesOf(vararg waypoints: Pose2D): List<ArcPose2D> =
        quinticSplinesOf(*waypoints).parameterized()