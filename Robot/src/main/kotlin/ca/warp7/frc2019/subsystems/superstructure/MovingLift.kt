package ca.warp7.frc2019.subsystems.superstructure

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Superstructure
import ca.warp7.frc2019.subsystems.SuperstructureState

object MovingLift : Action {
    val wantedPosition = WantedPosition()

    override fun start() {
        val setpoint = wantedPosition.toWantedLiftHeight()
    }

    override fun stop() {
        Superstructure.set(SuperstructureState.kHoldingPosition)
    }
}