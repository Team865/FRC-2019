package ca.warp7.frc2019.test.drive.velocity_control

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode

object VelocityControlLoop : Action {

    override fun start() {
        Drive.controlMode = ControlMode.Velocity
        Drive.set(LinearTrajectoryFollower())
    }

    override val shouldFinish: Boolean
        get() = false
}