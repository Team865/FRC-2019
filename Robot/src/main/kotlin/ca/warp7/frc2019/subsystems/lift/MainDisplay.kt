package ca.warp7.frc2019.subsystems.lift

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.HatchCargo

object MainDisplay : Subsystem (){
    override fun onOutput() {
        put("is bottom hatch", LiftMotionPlanner.setpointLevel == 0 && LiftMotionPlanner.setpointType == HatchCargo.Hatch)
        put("is bottom cargo", LiftMotionPlanner.setpointLevel == 0 && LiftMotionPlanner.setpointType == HatchCargo.Cargo)
        put("is middle hatch", LiftMotionPlanner.setpointLevel == 1 && LiftMotionPlanner.setpointType == HatchCargo.Hatch)
        put("is middle cargo", LiftMotionPlanner.setpointLevel == 1 && LiftMotionPlanner.setpointType == HatchCargo.Cargo)
        put("is top hatch", LiftMotionPlanner.setpointLevel == 2 && LiftMotionPlanner.setpointType == HatchCargo.Hatch)
        put("is top cargo", LiftMotionPlanner.setpointLevel == 2 && LiftMotionPlanner.setpointType == HatchCargo.Cargo)
    }
}