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
import edu.wpi.first.wpilibj.DigitalInput

@Suppress("MemberVisibilityCanBePrivate")
object Lift : Subsystem() {

    val master: TalonSRX = talonSRX(LiftConstants.kMaster, LiftConstants.kMasterTalonConfig)
            .followedBy(victorSPX(LiftConstants.kFollower, inverted = true))
            .apply { setSensorPhase(true) }

    val hallEffect = DigitalInput(LiftConstants.kHallEffect)

    var demand = 0.0
    var feedforward = 0.0
    var position = 0
    var velocity = 0
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

    override fun onDisabled() = master.neutralOutput()
    override fun onOutput() = master.set(controlMode, demand, DemandType.ArbitraryFeedForward, feedforward)

    override fun onMeasure(dt: Double) {
        position = master.selectedSensorPosition
        velocity = master.selectedSensorVelocity
        hallEffectTriggered = !hallEffect.get()
        LiftMotionPlanner.updateMeasurements(dt)
    }

    override fun onPostUpdate() {
        put("HallEffect", hallEffectTriggered)
        put("Demand", demand)
        put("Feedforward", feedforward)
        put("Raw ticks", position)
        put("Adjusted Height (encoder)", LiftMotionPlanner.adjustedPositionTicks)
        put("Adjusted Height (in)", LiftMotionPlanner.height)
        put("Velocity (in per s)", LiftMotionPlanner.velocity)
        put("Acceleration (in per s^2)", LiftMotionPlanner.acceleration)
        put("Cool Setpoint", LiftMotionPlanner.getCoolSetpoint())
        put("Setpoint", LiftMotionPlanner.setpointInches * LiftConstants.kTicksPerInch)
    }
}