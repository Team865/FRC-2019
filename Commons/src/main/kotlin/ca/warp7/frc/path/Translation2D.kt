package ca.warp7.frc.path

data class Translation2D(
        val x: Double,
        val y: Double
) {
    companion object {
        val identity = Translation2D(0.0, 0.0)
    }
}