package ca.warp7.frc.geometry

@Suppress("unused")
data class Pose2D(val translation: Translation2D, val rotation: Rotation2D) : State<Pose2D> {

    override fun unaryMinus(): Pose2D = inverse

    override fun unaryPlus(): Pose2D = copy

    override val copy: Pose2D get() = Pose2D(translation.copy, rotation.copy)

    override val isIdentity: Boolean
        get() = epsilonEquals(identity)

    override fun epsilonEquals(state: Pose2D, epsilon: Double): Boolean =
            translation.epsilonEquals(state.translation) && rotation.epsilonEquals(state.rotation)

    override fun epsilonEquals(state: Pose2D): Boolean = epsilonEquals(state, 1E-12)

    override fun transform(by: Pose2D): Pose2D =
            Pose2D(translation.transform(by.translation.rotate(by.rotation)), rotation.transform(by.rotation))

    override fun plus(by: Pose2D): Pose2D = transform(by)

    override fun minus(by: Pose2D): Pose2D = transform(by.inverse)

    override fun scaled(by: Double): Pose2D = Pose2D(translation.scaled(by), rotation.scaled(by))

    override fun times(by: Double): Pose2D = scaled(by)

    override fun div(by: Double): Pose2D = scaled(1.0 / by)

    override fun distanceTo(state: Pose2D): Double {
        TODO("not implemented")
    }

    override val state: Pose2D get() = this

    override fun rangeTo(state: Pose2D): Interpolator<Pose2D> {
        TODO("not implemented")
    }

    override fun interpolate(other: Pose2D, x: Double): Pose2D {
        TODO("not implemented")
    }

    override val inverse: Pose2D get() = rotation.inverse.let { Pose2D(translation.inverse.rotate(it), it) }

    override fun toString(): String {
        return "Pose($translation, $rotation)"
    }

    companion object {
        val identity = Pose2D(Translation2D.identity, Rotation2D.identity)
    }
}