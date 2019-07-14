package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f
import ca.warp7.frc.linearInterpolate

class ArcPose2D(
        val pose: Pose2D,
        val curvature: Double,
        val dk_ds: Double
) : State<ArcPose2D> {

    val translation: Translation2D = pose.translation
    val rotation: Rotation2D = pose.rotation

    override fun unaryMinus(): ArcPose2D = inverse

    override fun unaryPlus(): ArcPose2D = copy

    override val copy: ArcPose2D get() = ArcPose2D(pose, curvature, dk_ds)

    override val isIdentity: Boolean
        get() = epsilonEquals(identity)

    override fun epsilonEquals(state: ArcPose2D, epsilon: Double): Boolean =
            pose.epsilonEquals(state.pose, epsilon) &&
                    curvature.epsilonEquals(state.curvature, epsilon) &&
                    dk_ds.epsilonEquals(state.dk_ds, epsilon)

    override fun epsilonEquals(state: ArcPose2D): Boolean = epsilonEquals(state, 1E-12)

    override fun transform(by: ArcPose2D): ArcPose2D =
            ArcPose2D(pose.transform(by.pose), curvature, dk_ds)

    override fun plus(by: ArcPose2D): ArcPose2D = transform(by)

    override fun minus(by: ArcPose2D): ArcPose2D = transform(by.inverse)

    override fun scaled(by: Double): ArcPose2D =
            ArcPose2D(pose.scaled(by), curvature, dk_ds)

    override fun times(by: Double): ArcPose2D = scaled(by)

    override fun div(by: Double): ArcPose2D = scaled(1.0 / by)

    override fun distanceTo(state: ArcPose2D): Double = pose.distanceTo(state.pose)

    override val state: ArcPose2D get() = this

    override fun rangeTo(state: ArcPose2D): Interpolator<ArcPose2D> =
            object : Interpolator<ArcPose2D> {
                override fun get(x: Double): ArcPose2D = interpolate(state, x)
            }

    override fun interpolate(other: ArcPose2D, x: Double): ArcPose2D = when {
        x <= 0 -> copy
        x >= 1 -> other.copy
        else -> ArcPose2D(
                pose.interpolate(other.pose, x),
                linearInterpolate(curvature, other.curvature, x),
                linearInterpolate(dk_ds, other.dk_ds, x)
        )
    }

    override val inverse: ArcPose2D get() = ArcPose2D(pose.inverse, curvature, dk_ds)

    override fun toString(): String {
        return "Arc($pose, k=${curvature.f})"
    }

    companion object {
        val identity = ArcPose2D(Pose2D.identity, 0.0, 0.0)
    }
}