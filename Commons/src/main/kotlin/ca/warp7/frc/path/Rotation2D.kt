package ca.warp7.frc.path

@Suppress("unused")
data class Rotation2D(
        val cos: Double,
        val sin: Double
) {
    companion object {
        val identity = Rotation2D(1.0, 0.0)
    }
}