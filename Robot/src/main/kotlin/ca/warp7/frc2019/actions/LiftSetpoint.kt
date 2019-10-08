package ca.warp7.frc2019.actions

import ca.warp7.frc.action.Action
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Lift

class LiftSetpoint(var setpoint: Double = 0.0) : Action {
    private val io: BaseIO = ioInstance()

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