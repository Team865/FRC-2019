package ca.warp7.frc2019.subsystems.superstructure

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.LiftState
import ca.warp7.frc2019.subsystems.Superstructure
import ca.warp7.frc2019.subsystems.SuperstructureState

object MovingLift : Action {
    val wantedPosition = WantedPosition()
    var setpoint = 0.0

    override fun start() {
        setpoint = wantedPosition.toWantedLiftHeight()
    }

    override fun update() {
        Lift.set(LiftState.kGoToPosition){setHeightAbsoluteInches(setpoint)}
    }

    override val shouldFinish: Boolean
        get() {
            return false
        }

    override fun stop() {
        Superstructure.set(SuperstructureState.kHoldingPosition)
    }
}