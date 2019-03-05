package ca.warp7.frc2019.subsystems

import ca.warp7.actionkt.Action
import ca.warp7.frc.Subsystem
import ca.warp7.frc.followedBy
import ca.warp7.frc.talonSRX
import ca.warp7.frc.victorSPX
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.HatchCargo
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.lift.LiftMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.DigitalInput

@Suppress("MemberVisibilityCanBePrivate")
object Lift : Subsystem() {

    val master: TalonSRX = talonSRX(LiftConstants.kMaster, LiftConstants.kMasterTalonConfig)
            .followedBy(victorSPX(LiftConstants.kFollower, inverted = true))

    private val hallEffect = DigitalInput(LiftConstants.kHallEffect)

    var demand = 0.0
    var feedforward = 0.0
    var positionTicks = 0
    var velocityTicks = 0
    var actualPercent = 0.0
    var actualCurrent = 0.0
    var actualVoltage = 0.0
    var hallEffectTriggered = true

    var setpointLevel = 0
    var setpointType = HatchCargo.Hatch
    val coolSetpoint
        get() = LiftConstants.kTicksPerInch * (
                setpointLevel * FieldConstants.centerToCenterInches +
                        when (setpointType) {
                            HatchCargo.Hatch -> 0.0
                            HatchCargo.Cargo -> FieldConstants.hatchToCargoHeight + 0.5
                        }
                )

    var controlMode = ControlMode.PercentOutput
        set(value) {
            if (field != value) when (value) {
                ControlMode.Position -> master.selectProfileSlot(0, 0)
                ControlMode.Velocity -> master.selectProfileSlot(1, 0)
                else -> Unit
            }
            field = value
        }

    override fun <T : Action> set(wantedState: T, block: T.() -> Unit) {
        println("Setting lift to $wantedState")
        super.set(wantedState, block)
    }

    override fun onDisabled() {
        master.neutralOutput()
    }

    override fun onOutput() {
        master.set(controlMode, demand, DemandType.ArbitraryFeedForward, feedforward)
    }

    override fun onMeasure(dt: Double) {
        positionTicks = master.selectedSensorPosition
        velocityTicks = master.selectedSensorVelocity
        actualPercent = master.motorOutputPercent
//        actualCurrent = master.outputCurrent
//        actualVoltage = master.busVoltage * actualPercent
        hallEffectTriggered = !hallEffect.get()
        LiftMotionPlanner.updateMeasurements(dt)
    }

    override fun onPostUpdate() {
        put("Actual Percent", actualPercent)
//        put("Actual Current", actualCurrent)
//        put("Actual Voltage", actualVoltage)
        put("HallEffect", hallEffectTriggered)
        put("Demand", demand)
        put("Feedforward", feedforward)
        put("Height (encoder)", positionTicks)
        put("Height (in)", LiftMotionPlanner.height)
        put("Velocity (in per s)", LiftMotionPlanner.velocity)
        put("Acceleration (in per s^2)", LiftMotionPlanner.acceleration)
    }
}