package ca.warp7.frc2019.actions

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.Lift

class GoToSetpoint(var setpoint: Double = 0.0) : Action {
    private val io: RobotIO = RobotIO

    override val shouldFinish: Boolean
        get() = false

    override fun update() {
        Lift.setpointInches = setpoint
        Lift.updatePositionControl()
    }

    override fun stop() {
        io.liftDemand = Lift.setpointInches * LiftConstants.kTicksPerInch
        io.liftFeedforward = LiftConstants.kPrimaryFeedforward
    }
}