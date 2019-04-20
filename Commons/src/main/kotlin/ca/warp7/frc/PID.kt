package ca.warp7.frc

data class PID(
        var kP: Double = 0.0,
        var kI: Double = 0.0,
        var kD: Double = 0.0,
        var kF: Double = 0.0,
        val errorEpsilon: Double = 0.1,
        val dErrorEpsilon: Double = 0.1,
        val minTimeInEpsilon: Double = 0.1,
        val dtNormalizer: Double = 50.0,
        val maxOutput: Double = 1.0
) {

    var lastError = 0.0
    var dError = 0.0
    var sumError = 0.0
    var timeInEpsilon = 0.0
    var setpoint = 0.0
    var dt = 0.0

    fun updateByError(error: Double, dt: Double): Double {
        this.dt = dt
        return updateByError(error)
    }

    fun updateByError(error: Double): Double {
        // normalize the change in time so kD doesn't need to be too high and kI doesn't need to be too low
        val normalizedDt = dt * dtNormalizer
        // calculate proportional gain
        val pGain = kP * error
        // calculate conditions for resetting integral sum
        if (!pGain.epsilonEquals(0.0, maxOutput) // error is bigger than kP can handle
                || error.epsilonEquals(0.0, errorEpsilon) // error is smaller than the epsilon range
                || (pGain > 0 && sumError < 0) // sumError is in reverse while kP goes in forward
                || (pGain < 0 && sumError > 0) // sumError is in forward while kP goes in reverse
        ) sumError = 0.0
        // otherwise add current error to the sum of errors
        else sumError += error * normalizedDt
        // calculate the integral gain
        val iGain = kI * sumError
        // calculate change in error
        dError = (error - lastError) / normalizedDt
        lastError = error
        // calculate derivative gain
        val dGain = kD * dError
        // calculate the time when error and change in error is small enough
        // by adding the unmodified dt to a sum
        if (error.epsilonEquals(0.0, errorEpsilon)
                && dError.epsilonEquals(0.0, dErrorEpsilon)
        ) timeInEpsilon += dt
        // reset timeInEpsilon
        else timeInEpsilon = 0.0

        return (pGain + iGain + dGain).coerceIn(-maxOutput, maxOutput)
    }

    fun updateBySetpoint(actual: Double): Double {
        // calculate feedforward gain
        val fGain = kF * setpoint
        // calculate error
        val error = setpoint - actual
        // calculate feedback gains
        val pidGain = updateByError(error)
        // add up gains to get the output
        return (fGain + pidGain).coerceIn(-maxOutput, maxOutput)
    }

    fun isDone(): Boolean = timeInEpsilon > minTimeInEpsilon
}