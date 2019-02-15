package ca.warp7.frc2019.test.lift.feedforward

import ca.warp7.frc.ControllerState
import ca.warp7.frc.RobotControlLoop
import ca.warp7.frc.withDriver
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import kotlin.math.absoluteValue
import kotlin.math.withSign

object LiftFeedforwardLoop : RobotControlLoop {
    override fun setup() {
    }

    private var feedforward = 0.05
    private var olr = 0.5
    private var scale = 0.5

    private val master = LiftSubsystem.master

    override fun periodic() {
        withDriver {
            if (leftBumper == ControllerState.Pressed) feedforward -= 0.01
            if (rightBumper == ControllerState.Pressed) feedforward += 0.01
            feedforward = feedforward.coerceIn(0.05, 1.0)
            if (xButton == ControllerState.Pressed) scale += 0.1
            if (yButton == ControllerState.Pressed) scale -= 0.1
            scale = scale.coerceIn(0.0, 1.0)
            var newOLR = olr
            if (aButton == ControllerState.Pressed) newOLR += 0.1
            if (bButton == ControllerState.Pressed) newOLR -= 0.1
            newOLR = newOLR.coerceAtLeast(0.0)
            if (newOLR != olr) {
                master.configOpenloopRamp(newOLR, 0)
                olr = newOLR
            }
            if (startButton == ControllerState.Pressed) {
                feedforward = 0.05
                olr = 0.0
                master.configOpenloopRamp(olr, 0)
            }
            val y = leftYAxis
            master.set(ControlMode.PercentOutput,
                    if (y.absoluteValue > 0.2) -(y - 0.2.withSign(y)) / 0.8 * scale else 0.0,
                    DemandType.ArbitraryFeedForward,
                    feedforward)
        }
    }
}