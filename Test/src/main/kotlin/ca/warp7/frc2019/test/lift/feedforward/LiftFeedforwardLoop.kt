package ca.warp7.frc2019.test.lift.feedforward

import ca.warp7.frc.ControllerState
import ca.warp7.frc.RobotControlLoop
import ca.warp7.frc.getShuffleboardTab
import ca.warp7.frc.withDriver
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer
import kotlin.math.absoluteValue
import kotlin.math.withSign

object LiftFeedforwardLoop : RobotControlLoop {
    override fun setup() {
    }

    private var feedforward = 0.05
    private var ramp = 0.5
    private var scale = 0.5

    private val master = LiftSubsystem.master

    private val rampEntry: NetworkTableEntry
    private val feedforwardEntry: NetworkTableEntry
    private val scaleEntry: NetworkTableEntry

    init {
        val a: ShuffleboardContainer = getShuffleboardTab(LiftSubsystem)
        rampEntry = a.add("ramp", 0.0)
                .withPosition(0, 0).withSize(10, 3)
                .withWidget(BuiltInWidgets.kNumberSlider).entry
        feedforwardEntry = a.add("feedforward", 0.0)
                .withPosition(0, 3).withSize(10, 3)
                .withWidget(BuiltInWidgets.kNumberSlider).entry
        scaleEntry = a.add("scale", 0.0)
                .withPosition(0, 6).withSize(10, 3)
                .withWidget(BuiltInWidgets.kNumberSlider).entry
    }

    override fun periodic() {
        withDriver {
            if (leftBumper == ControllerState.Pressed) feedforward -= 0.01
            if (rightBumper == ControllerState.Pressed) feedforward += 0.01
            feedforward = feedforward.coerceIn(0.05, 1.0)
            if (xButton == ControllerState.Pressed) scale += 0.1
            if (yButton == ControllerState.Pressed) scale -= 0.1
            scale = scale.coerceIn(0.0, 1.0)
            var newRamp = ramp
            if (aButton == ControllerState.Pressed) newRamp += 0.1
            if (bButton == ControllerState.Pressed) newRamp -= 0.1
            newRamp = newRamp.coerceAtLeast(0.0)
            if (newRamp != ramp) {
                master.configOpenloopRamp(newRamp, 0)
                ramp = newRamp
            }
            if (startButton == ControllerState.Pressed) {
                feedforward = 0.05
                ramp = 0.0
                master.configOpenloopRamp(ramp, 0)
            }
            val y = leftYAxis
            master.set(ControlMode.PercentOutput,
                    if (y.absoluteValue > 0.2) -(y - 0.2.withSign(y)) / 0.8 * scale else 0.0,
                    DemandType.ArbitraryFeedForward, feedforward)
        }
        rampEntry.setDouble(ramp)
        feedforwardEntry.setDouble(feedforward)
        scaleEntry.setDouble(scale)
    }
}