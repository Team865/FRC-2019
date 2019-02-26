package ca.warp7.frc2019.test.lift_outtake.tuning

import ca.warp7.actionkt.Action
import ca.warp7.frc.ControllerState
import ca.warp7.frc.getShuffleboardTab
import ca.warp7.frc.withDriver
import ca.warp7.frc2019.subsystems.Conveyor
import ca.warp7.frc2019.subsystems.Outtake
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer
import kotlin.math.absoluteValue
import kotlin.math.withSign

object LiftOuttakeLoop : Action {

    private var outtake = 0.5
    private var conveyor = 0.5

    private val master = LiftSubsystem.master

    private val feedforwardEntry: NetworkTableEntry
    private val scaleEntry: NetworkTableEntry

    init {
        master.configOpenloopRamp(0.3)
        val a: ShuffleboardContainer = getShuffleboardTab(LiftSubsystem)
        feedforwardEntry = a.add("outtake", 0.0)
                .withPosition(0, 3).withSize(10, 3)
                .withWidget(BuiltInWidgets.kNumberSlider).entry
        scaleEntry = a.add("conveyor", 0.0)
                .withPosition(0, 6).withSize(10, 3)
                .withWidget(BuiltInWidgets.kNumberSlider).entry
    }

    override val shouldFinish: Boolean
        get() = false

    override fun update() {
        withDriver {
            if (leftBumper == ControllerState.Pressed) outtake -= 0.1
            if (rightBumper == ControllerState.Pressed) outtake += 0.1
            outtake = outtake.coerceIn(0.0, 1.0)
            if (aButton == ControllerState.Pressed) conveyor += 0.1
            if (bButton == ControllerState.Pressed) conveyor -= 0.1
            conveyor = conveyor.coerceIn(0.0, 1.0)
            val y = leftYAxis
            master.set(ControlMode.PercentOutput,
                    if (y.absoluteValue > 0.2) -(y - 0.2.withSign(y)) / 0.8 * 0.6 else 0.0,
                    DemandType.ArbitraryFeedForward, 0.08)
            when {
                leftTriggerAxis > 0.2 -> {
                    Outtake.speed = leftTriggerAxis * outtake
                    Conveyor.speed = -leftTriggerAxis * conveyor
                }
                rightTriggerAxis > 0.2 -> {
                    Outtake.speed = -rightTriggerAxis * outtake
                    Conveyor.speed = rightTriggerAxis * conveyor
                }
                else -> {
                    Outtake.speed = 0.0
                    Conveyor.speed = 0.0
                }
            }
        }
        feedforwardEntry.setDouble(outtake)
        scaleEntry.setDouble(conveyor)
    }
}