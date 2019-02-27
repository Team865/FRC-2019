package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.followedBy
import ca.warp7.frc.talonSRX
import ca.warp7.frc.victorSPX
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.lift.LiftMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.can.TalonSRX

@Suppress("MemberVisibilityCanBePrivate")
object Lift : Subsystem() {

    private val master: TalonSRX = talonSRX(LiftConstants.kMaster, LiftConstants.kMasterTalonConfig)
            .followedBy(victorSPX(LiftConstants.kFollower, inverted = true))

    // private val hallEffect = DigitalInput(LiftConstants.kHallEffect)

    var demand = 0.0
    var feedforward = 0.0
    var positionTicks = 0
    var velocityTicksPer100ms = 0
    var actualPercent = 0.0
    var actualCurrent = 0.0
    var actualVoltage = 0.0
    var hallEffectTriggered = true

    var controlMode = ControlMode.PercentOutput
        set(value) {
            if (field != value) when (value) {
                ControlMode.Position -> master.selectProfileSlot(0, 0)
                ControlMode.Velocity -> master.selectProfileSlot(1, 0)
                else -> Unit
            }
            field = value
        }

    override fun onDisabled() {
        master.neutralOutput()
    }

    override fun onOutput() {
        master.set(controlMode, demand, DemandType.ArbitraryFeedForward, feedforward)
    }

    override fun onMeasure(dt: Double) {
        positionTicks = master.selectedSensorPosition
        velocityTicksPer100ms = master.selectedSensorVelocity
        actualPercent = master.motorOutputPercent
        actualCurrent = master.outputCurrent
        actualVoltage = master.busVoltage * actualPercent
        hallEffectTriggered = true // hallEffect.get()
        LiftMotionPlanner.updateMeasurements(dt)
    }

    override fun onPostUpdate() {
        put("Actual Percent", actualPercent)
        put("Actual Current", actualCurrent)
        put("Actual Voltage", actualVoltage)
        put("Demand", demand)
        put("Feedforward", feedforward)
        put("Height (encoder)", positionTicks)
        put("Height (in)", LiftMotionPlanner.height)
        put("Velocity (in per s)", LiftMotionPlanner.velocity)
        put("Acceleration (in per s^2)", LiftMotionPlanner.acceleration)
    }
}