package ca.warp7.frc

class PID(var pidValues: PIDValues) {
    var zeroOffset: Double = 0.0
    private var relTarget = 0.0
    private var prevError: Double = 0.0

    var sumError: Double = 0.0
    var error: Double = 0.0
    var dError: Double = 0.0

    fun setTarget(target: Double) {
        relTarget = target - zeroOffset
    }

    fun calc(curState: Double, dt: Double = 1.0): Double {
        error = relTarget - curState
        sumError += error * dt
        dError = (error - prevError) / dt

        prevError = error

        pidValues.apply {
            return p * error + i * sumError + d * dError
        }
    }
}