package ca.warp7.frc.path

import ca.warp7.frc.geometry.ArcPose2D
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.rotate

fun List<QuinticSegment2D>.parameterized(): List<ArcPose2D> {
    val points = mutableListOf<ArcPose2D>()
    val p0 = first()[0.0]
    points.add(ArcPose2D(p0.toPose(), p0.curvature, 0.0))
    forEach { points.addAll(it.parameterized()) }
    return points
}

fun QuinticSegment2D.parameterized(): List<ArcPose2D> =
        parameterized(0.1, 0.01, 0.1)

fun QuinticSegment2D.parameterized(maxDx: Double, maxDy: Double, maxDTheta: Double): List<ArcPose2D> {
    val points = mutableListOf<ArcPose2D>()
    parameterize(points, 0.0, 1.0, maxDx, maxDy, maxDTheta)
    return points
}

fun QuinticSegment2D.parameterize(
        points: MutableList<ArcPose2D>,
        t0: Double,
        t1: Double,
        maxDx: Double,
        maxDy: Double,
        maxDTheta: Double
) {
    val p0 = get(t0)
    val p1 = get(t1)
    val heading0 = p0.heading
    val heading1 = p1.heading
    val point0 = p0.point
    val point1 = p1.point
    // get the twist transformation between start and and points
    val twist = Pose2D((point1 - point0).rotate(-heading0), heading1 - heading0).log
    // check if the twist is within threshold
    if (twist.dy > maxDy || twist.dx > maxDx || twist.dTheta > maxDTheta) {
        // partition and re-parameterize
        parameterize(points, t0, (t0 + t1) / 2.0, maxDx, maxDy, maxDTheta)
        parameterize(points, (t0 + t1) / 2.0, t1, maxDx, maxDy, maxDTheta)
    } else {
        points.add(ArcPose2D(Pose2D(point1, heading1), p1.curvature, 0.0))
    }
}