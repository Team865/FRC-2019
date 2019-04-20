package ca.warp7.frc2019.subsystems

import ca.warp7.frc.control.Subsystem
import ca.warp7.frc.control.victorSPX
import ca.warp7.frc2019.constants.ConveyorConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object Conveyor : Subsystem() {
    private val leftMaster: VictorSPX = victorSPX(ConveyorConstants.kLeftMaster)
    private val rightMaster: VictorSPX = victorSPX(ConveyorConstants.kRightMaster)

    var speed = 0.0

    override fun onDisabled() {
        leftMaster.neutralOutput()
        rightMaster.neutralOutput()
    }

    override fun onOutput() {
        leftMaster.set(ControlMode.PercentOutput, speed)
        // NOTE unlike the outtake, the right master is not reversed
        rightMaster.set(ControlMode.PercentOutput, speed)
    }
}