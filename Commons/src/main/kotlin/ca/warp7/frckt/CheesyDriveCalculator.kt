package ca.warp7.frckt

class CheesyDriveCalculator(private val mReceiver: (left: Double, right: Double) -> Unit) {

    private var quickStopAccumulator = 0.0
    private var oldWheel = 0.0
    private var mWheel: Double = 0.0
    private var mThrottle: Double = 0.0
    private var quickTurn: Boolean = false


    private fun wrapAccumulator(accumulator: Double): Double {
        return when {
            accumulator > 1 -> accumulator - 1.0
            accumulator < -1 -> accumulator + 1.0
            else -> 0.0
        }
    }

    private fun sinScale(value: Double, nonLinearity: Double, passes: Int, lim: Double): Double {
        val scaled = lim * Math.sin(Math.PI / 2 * nonLinearity * value) / Math.sin(Math.PI / 2 * nonLinearity)
        return if (passes == 1) scaled else sinScale(scaled, nonLinearity, passes - 1, lim)
    }

    fun cheesyDrive(wheel: Double, throttle: Double, isQuickTurn: Boolean) {
        setInputs(wheel, throttle, isQuickTurn)
        calculate()
    }

    private fun setInputs(wheel: Double, throttle: Double, isQuickTurn: Boolean) {
        mWheel = wheel
        mThrottle = throttle
        quickTurn = isQuickTurn
    }

    private fun calculate() {
        var rightPwm: Double
        var leftPwm: Double
        val negInertiaScalar: Double
        val negInertia: Double
        val negInertiaAccumulator: Double
        val overPower: Double
        val angularPower: Double
        var wheel = mWheel
        val throttle = mThrottle

        negInertia = wheel - oldWheel
        oldWheel = wheel

        wheel = sinScale(wheel, 0.9, 1, 0.9)
        negInertiaScalar = (if (wheel * negInertia > 0) 2.5 else if (Math.abs(wheel) > .65) 6.0 else 4.0)
        negInertiaAccumulator = negInertia * negInertiaScalar
        wheel += negInertiaAccumulator

        if (quickTurn) {
            if (Math.abs(throttle) < 0.2) {
                val alpha = .1
                quickStopAccumulator = (1 - alpha) * quickStopAccumulator + alpha * limit(wheel, 1.0) * 5.0
            }
            overPower = 1.0
            angularPower = -wheel * 1

        } else {
            overPower = 0.0
            val sensitivity = .9
            angularPower = throttle * wheel * sensitivity - quickStopAccumulator
            quickStopAccumulator = wrapAccumulator(quickStopAccumulator)
        }

        leftPwm = throttle
        rightPwm = leftPwm
        leftPwm += angularPower
        rightPwm -= angularPower

        if (leftPwm > 1) {
            rightPwm -= overPower * (leftPwm - 1)
            leftPwm = 1.0
        } else if (rightPwm > 1) {
            leftPwm -= overPower * (rightPwm - 1)
            rightPwm = 1.0
        } else if (leftPwm < -1) {
            rightPwm += overPower * (-1 - leftPwm)
            leftPwm = -1.0
        } else if (rightPwm < -1) {
            leftPwm += overPower * (-1 - rightPwm)
            rightPwm = -1.0
        }

        mReceiver.invoke(leftPwm, rightPwm)
    }
}
