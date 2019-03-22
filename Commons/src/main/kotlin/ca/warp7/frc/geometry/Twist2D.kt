package ca.warp7.frc.geometry

import ca.warp7.frc.f

@Suppress("unused")
data class Twist2D(val dx: Double, val dy: Double, val dTheta: Double) {
    override fun toString(): String {
        return "Twist(${dx.f}, ${dy.f}, ${dTheta.f})"
    }

    val exp: Pose2D
        get() {
            val sinTheta = Math.sin(dTheta)
            val cosTheta = Math.cos(dTheta)
            val s: Double
            val c: Double
            if (Math.abs(dTheta) < 1E-9) {
                s = 1.0 - 1.0 / 6.0 * dTheta * dTheta
                c = .5 * dTheta
            } else {
                s = sinTheta / dTheta
                c = (1.0 - cosTheta) / dTheta
            }
            return Pose2D(Translation2D(dx * s - dy * c, dx * c + dy * s), Rotation2D(cosTheta, sinTheta))
        }
}