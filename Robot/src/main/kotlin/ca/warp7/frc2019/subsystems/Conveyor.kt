package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.ConveyorConstants
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object Conveyor : Subsystem() {
    private val leftConveyor = VictorSPX(ConveyorConstants.kLeftConveyor)
    private val rightConveyor = VictorSPX(ConveyorConstants.kRightConveyor)
}