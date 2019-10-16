@file:Suppress("unused", "DuplicatedCode")

package ca.warp7.frc2019.helper

import ca.warp7.frc.control.PID
import ca.warp7.frc.epsilonEquals
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.StatusFrame
import com.ctre.phoenix.motorcontrol.can.*

fun <T : TalonSRX> T.config(config: TalonSRXConfiguration) = apply { configAllSettings(config) }

fun <T : VictorSPX> T.config(config: VictorSPXConfiguration) = apply { configAllSettings(config) }

fun <T : BaseMotorController> T.followedBy(vararg other: BaseMotorController) =
        apply { other.forEach { it.reset().follow(this) } }

fun <T : BaseMotorController> T.reset() = apply {
    configFactoryDefault()
    selectProfileSlot(0, 0)
    setNeutralMode(NeutralMode.Brake)
}

fun <T : BaseMotorController> T.setPID(pid: PID) = apply {
    selectProfileSlot(0, 0)
    config_kP(0, pid.kP, 0)
    config_kI(0, pid.kI, 0)
    config_kD(0, pid.kD, 0)
    config_kF(0, pid.kF, 0)
}

private class LazyTalonSRX(deviceNumber: Int) : TalonSRX(deviceNumber) {

    var previousMode = ControlMode.PercentOutput
    var previousDemand0 = 0.0
    var previousDemand1Type = DemandType.Neutral
    var previousDemand1 = 0.0

    override fun set(mode: ControlMode, demand0: Double, demand1Type: DemandType, demand1: Double) {
        if (mode != previousMode
                || !demand0.epsilonEquals(previousDemand0, 1E-12)
                || demand1Type != previousDemand1Type
                || !demand1.epsilonEquals(previousDemand1, 1E-12)) {
            previousMode = mode
            previousDemand0 = demand0
            previousDemand1Type = demand1Type
            previousDemand1 = demand1
            super.set(mode, demand0, demand1Type, demand1)
        }
    }
}

fun talonSRX(
        id: Int,
        config: TalonSRXConfiguration? = null,
        neutralMode: NeutralMode = NeutralMode.Brake,
        voltageCompensation: Boolean = true,
        currentLimit: Boolean = false,
        inverted: Boolean = false
): TalonSRX = LazyTalonSRX(id).apply {
    if (config == null) configFactoryDefault(50)
    else configAllSettings(config, 50)
    setNeutralMode(neutralMode)
    enableVoltageCompensation(voltageCompensation)
    enableCurrentLimit(currentLimit)
    setInverted(inverted)
    selectedSensorPosition = 0
    selectProfileSlot(0, 0)
    setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 10)
}

private class LazyVictorSPX(deviceNumber: Int) : VictorSPX(deviceNumber) {

    var previousMode = ControlMode.PercentOutput
    var previousDemand0 = 0.0
    var previousDemand1Type = DemandType.Neutral
    var previousDemand1 = 0.0

    override fun set(mode: ControlMode, demand0: Double, demand1Type: DemandType, demand1: Double) {
        if (mode != previousMode
                || !demand0.epsilonEquals(previousDemand0, 1E-12)
                || demand1Type != previousDemand1Type
                || !demand1.epsilonEquals(previousDemand1, 1E-12)) {
            previousMode = mode
            previousDemand0 = demand0
            previousDemand1Type = demand1Type
            previousDemand1 = demand1
            super.set(mode, demand0, demand1Type, demand1)
        }
    }
}

fun victorSPX(
        id: Int,
        config: VictorSPXConfiguration? = null,
        neutralMode: NeutralMode = NeutralMode.Brake,
        voltageCompensation: Boolean = false,
        inverted: Boolean = false
): VictorSPX = LazyVictorSPX(id).apply {
    if (config == null) configFactoryDefault(50)
    else configAllSettings(config, 50)
    setNeutralMode(neutralMode)
    enableVoltageCompensation(voltageCompensation)
    setInverted(inverted)
    selectProfileSlot(0, 0)
}