package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.lazyVictorSPX
import ca.warp7.frc2019.constants.ConveyorConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object Conveyor : Subsystem() {
    private val leftConveyor: VictorSPX = lazyVictorSPX(ConveyorConstants.kLeft)
    private val rightConveyor: VictorSPX = lazyVictorSPX(ConveyorConstants.kRight)

    init {
        leftConveyor.setNeutralMode(NeutralMode.Brake)
        rightConveyor.setNeutralMode(NeutralMode.Brake)
    }

    var speed = 0.0

    override fun onDisabled() {
        leftConveyor.neutralOutput()
        rightConveyor.neutralOutput()
    }

    override fun onOutput() {
        leftConveyor.set(ControlMode.PercentOutput, speed)
        rightConveyor.set(ControlMode.PercentOutput, speed)
    }
}