package ca.warp7.frc

import com.ctre.phoenix.motorcontrol.can.*

fun <T : TalonSRX> T.config(config: TalonSRXConfiguration) = apply { configAllSettings(config) }
fun <T : VictorSPX> T.config(config: VictorSPXConfiguration) = apply { configAllSettings(config) }
fun <T : BaseMotorController> T.followedBy(other: BaseMotorController) = apply { other.follow(this) }
