package ca.warp7.frc2019.test.drive.velocity_control

import ca.warp7.actionkt.Action
import ca.warp7.frc.set
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode

object VelocityControlLoop : Action {

    override fun start() {
        Drive.set {
            leftMaster.configFactoryDefault(0)
            rightMaster.configFactoryDefault(0)
            leftMaster.enableVoltageCompensation(false)
            rightMaster.enableVoltageCompensation(false)
            leftMaster.config_kP(1, 0.5, 0)
            leftMaster.config_kI(1, 0.0, 0)
            leftMaster.config_kD(1, 5.0, 0)
            leftMaster.config_kF(1, 1.0, 0)
            leftMaster.configOpenloopRamp(0.0, 0)
            rightMaster.config_kP(1, 0.5, 0)
            rightMaster.config_kI(1, 0.0, 0)
            rightMaster.config_kD(1, 5.0, 0)
            rightMaster.config_kF(1, 1.0, 0)
            rightMaster.configOpenloopRamp(0.0, 0)
            leftMaster.selectProfileSlot(1, 0)
            rightMaster.selectProfileSlot(1, 0)
        }
        Drive.controlMode = ControlMode.Velocity
        //Drive.set(LinearTrajectoryFollower())
        //Drive.set(SimpleTrapezoid)
    }

    override fun update() {
        Drive.leftDemand = -200.0
        Drive.rightDemand = -300.0
        Drive.leftFeedforward = 0.0
        Drive.rightFeedforward = 0.0
    }

    override val shouldFinish: Boolean
        get() = false
}