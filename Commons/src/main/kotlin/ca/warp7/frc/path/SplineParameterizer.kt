package ca.warp7.frc.path

import ca.warp7.frc.geometry.CurvatureState
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.Twist2D
import ca.warp7.frc.geometry.rotate

fun List<QuinticSegment2D>.parameterized() = map { it.parameterized() }.flatten()

fun QuinticSegment2D.parameterized(): List<CurvatureState<Pose2D>> =
        parameterized(threshold = Twist2D(0.0254, 0.0254, 0.1))

fun QuinticSegment2D.parameterized(threshold: Twist2D): List<CurvatureState<Pose2D>> {
    val points = mutableListOf<CurvatureState<Pose2D>>()
    val p0 = get(0.0)
    points.add(CurvatureState(p0.toPose(), p0.curvature, p0.dk_ds))
    parameterize(points, 0.0, 1.0, threshold)
    return points
}

/**
 * Runs a recursive arc length parameterization
 */
fun QuinticSegment2D.parameterize(
        points: MutableList<CurvatureState<Pose2D>>,
        t0: Double,
        t1: Double,
        threshold: Twist2D
) {
    val p0 = get(t0)
    val p1 = get(t1)
    val heading0 = p0.heading
    // get the twist transformation between start and and points
    val twist = Pose2D((p1.point - p0.point).rotate(-heading0), p1.heading - heading0).log
    // check if the twist is within threshold
    if (twist.dy > threshold.dy || twist.dx > threshold.dx || twist.dTheta > threshold.dTheta) {
        // partition and re-parameterize
        parameterize(points, t0, (t0 + t1) / 2, threshold)
        parameterize(points, (t0 + t1) / 2, t1, threshold)
    } else {
        points.add(CurvatureState(p1.toPose(), p1.curvature, p1.dk_ds))
    }
}