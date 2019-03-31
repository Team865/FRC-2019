package ca.warp7.frc

class PID(val p: Double = 0.0, val i: Double = 0.0, val d: Double = 0.0) {
    var initPosition: Double = 0.0
    private var relTarget = 0.0

    private var sumError: Double = 0.0
    private var prevError: Double = 0.0

    fun setTarget(target: Double) {
        relTarget = target - initPosition
    }

    fun calc(pos: Double, dt: Double = 1.0): Double {
        val error = relTarget - pos
        sumError += error * dt
        val dError = (error-prevError) / dt
        prevError = error

        return p * error + i * sumError + d * dError
    }
}