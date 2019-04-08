package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.subsystems.superstructure.PassThrough

object Superstructure : Subsystem() {
    override fun onPostUpdate() {
        put("Conveyor Speed", Conveyor.speed)
        put("Outtake Speed", Outtake.speed)
        put("Outtake Grabbing", Outtake.grabbing)
        put("Outtake Pushing", Outtake.pushing)
        put("Intake Speed", Intake.speed)
        put("Intake Extended", Intake.extended)
        put("PassThrough Speed", PassThrough.speed)
    }
}