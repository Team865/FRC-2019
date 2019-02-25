package ca.warp7.frc.path

interface Path2D {
    fun px(t: Double): Double
    fun py(t: Double): Double
    fun vx(t: Double): Double
    fun vy(t: Double): Double
    fun ax(t: Double): Double
    fun ay(t: Double): Double
    fun jx(t: Double): Double
    fun jy(t: Double): Double
    val dx: Double
    val dy: Double
}