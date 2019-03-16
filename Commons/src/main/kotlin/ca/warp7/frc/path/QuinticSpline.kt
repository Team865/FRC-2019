package ca.warp7.frc.path

import kotlin.math.pow

data class QuinticSpline(
        private val p0: Double,
        private val m0: Double,
        private val dm0: Double,
        private val p1: Double,
        private val m1: Double,
        private val dm1: Double
) {

    private val a: Double = -6 * p0 - 3 * m0 - 0.5 * dm0 + 0.5 * dm1 - 3 * m1 + 6 * p1
    private val b: Double = 15 * p0 + 8 * m0 + 1.5 * dm0 - dm1 + 7 * m1 - 15 * p1
    private val c: Double = -10 * p0 - 6 * m0 - 1.5 * dm0 + 0.5 * dm1 - 4 * m1 + 10 * p1
    private val d: Double = 0.5 * dm0
    private val e: Double = m0
    private val f: Double = p0

    fun p(t: Double) = a * t.pow(5) + b * t.pow(4) + c * t.pow(3) + d * t.pow(2) + e * t + f
    fun v(t: Double) = 5 * a * t.pow(4) + 4 * b * t.pow(3) + 3 * c * t.pow(2) + 2 * d * t + e
    fun a(t: Double) = 20 * a * t.pow(3) + 12 * b * t.pow(2) + 6 * c * t + 2 * d
    fun j(t: Double) = 60 * a * t.pow(2) + 24 * b * t + 6 * c
}