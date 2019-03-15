package ca.warp7.frc.geometry

@Suppress("unused")
data class Rotation2D(val cos: Double, val sin: Double) {

    val copy: Rotation2D get() = Rotation2D(cos, sin)

    val inverse: Rotation2D get() = Rotation2D(cos, -sin)

    val normal: Rotation2D get() = Rotation2D(-sin, cos)

    override fun toString(): String {
        return "Rotation2D(${"%.3f".format(cos)}, ${"%.3f".format(sin)}, ${"%.3f".format(degrees)}°)"
    }

    companion object {
        val identity = Rotation2D(1.0, 0.0)
    }
}