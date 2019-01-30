package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.SuperstructureConstants
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object Superstructure : Subsystem() {
    private val leftConveyor = VictorSPX(SuperstructureConstants.kLeftConveyor)
    private val rightConveyor = VictorSPX(SuperstructureConstants.kRightConveyor)
}