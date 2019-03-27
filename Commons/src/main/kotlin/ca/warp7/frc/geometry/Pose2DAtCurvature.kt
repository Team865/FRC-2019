package ca.warp7.frc.geometry

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.f
import ca.warp7.frc.interpolate

@Suppress("unused")
data class Pose2DAtCurvature(val pose: Pose2D, val k: Double, val dk: Double) : State<Pose2DAtCurvature> {

    override fun unaryMinus(): Pose2DAtCurvature = inverse

    override fun unaryPlus(): Pose2DAtCurvature = copy

    override val copy: Pose2DAtCurvature get() = Pose2DAtCurvature(pose.copy, k, dk)

    override val isIdentity: Boolean
        get() = epsilonEquals(identity)

    override fun epsilonEquals(state: Pose2DAtCurvature, epsilon: Double): Boolean =
            pose.epsilonEquals(state.pose) && k.epsilonEquals(state.k, epsilon) && dk.epsilonEquals(state.dk, epsilon)

    override fun epsilonEquals(state: Pose2DAtCurvature): Boolean = epsilonEquals(state, 1E-12)

    override fun transform(by: Pose2DAtCurvature): Pose2DAtCurvature =
            Pose2DAtCurvature(pose.transform(by.pose), k, dk)

    override fun plus(by: Pose2DAtCurvature): Pose2DAtCurvature = transform(by)

    override fun minus(by: Pose2DAtCurvature): Pose2DAtCurvature = transform(by.inverse)

    override fun scaled(by: Double): Pose2DAtCurvature = Pose2DAtCurvature(pose.scaled(by), k, dk)

    override fun times(by: Double): Pose2DAtCurvature = scaled(by)

    override fun div(by: Double): Pose2DAtCurvature = scaled(1.0 / by)

    override fun distanceTo(state: Pose2DAtCurvature): Double = pose.distanceTo(state.pose)

    override val state: Pose2DAtCurvature get() = this

    override fun rangeTo(state: Pose2DAtCurvature): Interpolator<Pose2DAtCurvature> =
            object : Interpolator<Pose2DAtCurvature> {
                override fun get(x: Double): Pose2DAtCurvature = interpolate(state, x)
            }

    override fun interpolate(other: Pose2DAtCurvature, x: Double): Pose2DAtCurvature = when {
        x <= 0 -> copy
        x >= 1 -> other.copy
        else -> Pose2DAtCurvature(
                pose.interpolate(other.pose, x),
                interpolate(k, other.k, x),
                interpolate(dk, other.dk, x)
        )
    }

    override val inverse: Pose2DAtCurvature get() = Pose2DAtCurvature(pose.inverse, k, dk)

    override fun toString(): String {
        return "PoseAtCurvature($pose, k=${k.f}, dk=${dk.f})"
    }

    val mirrored: Pose2DAtCurvature get() = Pose2DAtCurvature(pose.mirrored, -k, -dk)

    companion object {
        val identity = Pose2DAtCurvature(Pose2D.identity, 0.0, 0.0)
    }
}