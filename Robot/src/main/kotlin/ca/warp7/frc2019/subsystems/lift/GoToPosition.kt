package ca.warp7.frc2019.subsystems.lift

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.LiftConstants.kHomeHeightInches
import ca.warp7.frc2019.subsystems.Lift
import com.ctre.phoenix.motorcontrol.ControlMode


object GoToPosition : Action {
    var heightInputAbsoluteInches = 0.0
    var targetHeightFromHome = 0.0

    fun setHeightAbsoluteInches(height : Double) {
        targetHeightFromHome = height - kHomeHeightInches
    }

    override fun update(){
        Lift.controlMode = ControlMode.Position
        Lift.demand = targetHeightFromHome - heightInputAbsoluteInches
        Lift.demand = targetHeightFromHome
    }

    override fun stop() {
    }
}