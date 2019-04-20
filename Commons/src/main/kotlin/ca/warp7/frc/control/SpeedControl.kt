@file:Suppress("unused")

package ca.warp7.frc.control

import edu.wpi.first.wpilibj.SpeedController
import edu.wpi.first.wpilibj.TimedRobot
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.withSign

private class LinearRampedSpeedController(ramp: Double, val setter: (Double) -> Unit) : SpeedController {

    var lastSpeed = 0.0
    var sign = 1.0
    val rate = TimedRobot.kDefaultPeriod / ramp

    override fun getInverted(): Boolean = sign > 0

    override fun pidWrite(output: Double) = set(output)

    override fun stopMotor() = disable()

    override fun get(): Double = lastSpeed * sign

    override fun set(speed: Double) {
        val diff = speed - lastSpeed
        lastSpeed += min(diff.absoluteValue, rate).withSign(diff)
        setter(lastSpeed * sign)
    }

    override fun setInverted(isInverted: Boolean) {
        sign = if (isInverted) -1.0 else 1.0
    }

    override fun disable() {
        lastSpeed = 0.0
    }
}

fun linearRamp(ramp: Double, setter: (Double) -> Unit): SpeedController = LinearRampedSpeedController(ramp, setter)


private class SpeedControllerImpl(val setter: (Double) -> Unit) : SpeedController {

    var lastSpeed = 0.0
    var sign = 1.0

    override fun getInverted(): Boolean = sign > 0

    override fun pidWrite(output: Double) = set(output)

    override fun stopMotor() = disable()

    override fun get(): Double = lastSpeed * sign

    override fun set(speed: Double) {
        lastSpeed = speed
        setter(lastSpeed * sign)
    }

    override fun setInverted(isInverted: Boolean) {
        sign = if (isInverted) -1.0 else 1.0
    }

    override fun disable() {
        lastSpeed = 0.0
    }
}

fun speedController(setter: (Double) -> Unit): SpeedController = SpeedControllerImpl(setter)