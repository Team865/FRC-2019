package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.lazyTalonSRX
import ca.warp7.frc.reset
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.lift.LiftMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX

@Suppress("MemberVisibilityCanBePrivate")
object Lift : Subsystem() {

    private val master: TalonSRX = lazyTalonSRX(
            id = LiftConstants.kMaster,
            config = LiftConstants.kMasterTalonConfig,
            voltageCompensation = true,
            currentLimit = false
    )

    init {
        val victor = VictorSPX(LiftConstants.kFollower)
        victor.reset()
        victor.setNeutralMode(NeutralMode.Brake)
        victor.inverted = true
        victor.follow(master)
    }

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
        master.set(controlMode, -demand, DemandType.ArbitraryFeedForward, feedforward)
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
        put("Output Type", controlMode.name)
        put("Actual Percent", actualPercent)
        put("Actual Current", actualCurrent)
        put("Actual Voltage", actualVoltage)
        put("Demand", demand)
        put("Feedforward", feedforward)
        put("Height (in)", LiftMotionPlanner.height)
        put("Velocity (in/s)", LiftMotionPlanner.velocity)
        put("Acceleration (in/s^2)", LiftMotionPlanner.acceleration)
    }
}