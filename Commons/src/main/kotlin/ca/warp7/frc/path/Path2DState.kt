package ca.warp7.frc.path

data class Path2DState(
        val t: Double,
        val x: Double,
        val y: Double,
        val vx: Double,
        val vy: Double,
        val ax: Double,
        val ay: Double,
        val jx: Double,
        val jy: Double
)