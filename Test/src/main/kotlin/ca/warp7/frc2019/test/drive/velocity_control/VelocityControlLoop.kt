package ca.warp7.frc2019.test.drive.velocity_control

import ca.warp7.actionkt.Action
import ca.warp7.frc.set
import ca.warp7.frc2019.subsystems.Drive

object VelocityControlLoop : Action {

    override fun start() {
        Drive.set {
            leftMaster.config_kP(1, 1.0)
            leftMaster.config_kI(1, 0.0)
            leftMaster.config_kD(1, 1.0)
            leftMaster.config_kF(1, 0.0)
            leftMaster.configOpenloopRamp(0.0)
            rightMaster.config_kP(1, 1.0)
            rightMaster.config_kI(1, 0.0)
            rightMaster.config_kD(1, 1.0)
            rightMaster.config_kF(1, 0.0)
            rightMaster.configOpenloopRamp(0.0)
        }
        Drive.set(SimpleTrapezoid)
    }
}