package ca.warp7.frc2019.subsystems.lift.deprecated

import ca.warp7.frc.control.Subsystem
import ca.warp7.frc2019.constants.HatchCargo
import ca.warp7.frc2019.subsystems.Lift

object MainDisplay : Subsystem() {
    override fun onOutput() {
        put("is bottom hatch", Lift.setpointLevel == 0 && Lift.setpointType == HatchCargo.Hatch)
        put("is bottom cargo", Lift.setpointLevel == 0 && Lift.setpointType == HatchCargo.Cargo)
        put("is middle hatch", Lift.setpointLevel == 1 && Lift.setpointType == HatchCargo.Hatch)
        put("is middle cargo", Lift.setpointLevel == 1 && Lift.setpointType == HatchCargo.Cargo)
        put("is top hatch", Lift.setpointLevel == 2 && Lift.setpointType == HatchCargo.Hatch)
        put("is top cargo", Lift.setpointLevel == 2 && Lift.setpointType == HatchCargo.Cargo)
    }
}