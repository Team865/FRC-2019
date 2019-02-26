package ca.warp7.frc.geometry

@Suppress("unused")
data class Pose2D(
        val translation: Translation2D,
        val rotation: Rotation2D
) {
    companion object {
        val identity = Pose2D(Translation2D.identity, Rotation2D.identity)
    }
}