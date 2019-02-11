@file:Suppress("unused")

package ca.warp7.frc

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.*
import edu.wpi.first.wpilibj.SpeedController

fun <T : TalonSRX> T.config(config: TalonSRXConfiguration) = apply { configAllSettings(config) }
fun <T : VictorSPX> T.config(config: VictorSPXConfiguration) = apply { configAllSettings(config) }
fun <T : BaseMotorController> T.followedBy(other: BaseMotorController) = apply { other.follow(this.reset()) }
fun <T : BaseMotorController> T.reset() = apply { configFactoryDefault() }

private class WPISpeedController(val base: BaseMotorController) : SpeedController {
    var lastSpeed = 0.0
    override fun getInverted(): Boolean = base.inverted
    override fun pidWrite(output: Double) = set(output)
    override fun stopMotor() = base.neutralOutput()
    override fun get(): Double = lastSpeed
    override fun disable() = base.neutralOutput()

    override fun set(speed: Double) {
        lastSpeed = speed
        base.set(ControlMode.PercentOutput, speed)
    }

    override fun setInverted(isInverted: Boolean) {
        base.inverted = isInverted
    }
}

val <T : BaseMotorController> T.wpi: SpeedController get() = WPISpeedController(this)