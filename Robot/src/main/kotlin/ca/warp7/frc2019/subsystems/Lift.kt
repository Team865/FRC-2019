package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.followedBy
import ca.warp7.frc.talonSRX
import ca.warp7.frc.victorSPX
import ca.warp7.frc2019.constants.FieldConstants.firstCargoBayCenterHeightInches
import ca.warp7.frc2019.constants.FieldConstants.secondCargoBayCenterHeightInches
import ca.warp7.frc2019.constants.FieldConstants.secondHatchPortCenterHeightInches
import ca.warp7.frc2019.constants.FieldConstants.thirdCargoBayCenterHeightInches
import ca.warp7.frc2019.constants.FieldConstants.thirdHatchPortCenterHeightInches
import ca.warp7.frc2019.constants.HatchCargo
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.constants.LiftConstants.kHomeHeightInches
import ca.warp7.frc2019.subsystems.lift.LiftMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.wpilibj.DigitalInput

@Suppress("MemberVisibilityCanBePrivate")
object Lift : Subsystem() {

    val master: TalonSRX = talonSRX(LiftConstants.kMaster, LiftConstants.kMasterTalonConfig)
            .followedBy(victorSPX(LiftConstants.kFollower, inverted = true))

    init {
        master.setSensorPhase(true)
    }

    private val hallEffect = DigitalInput(LiftConstants.kHallEffect)

    var demand = 0.0
    var feedforward = 0.0
    var actualPositionTicks = 0
    var velocityTicks = 0
    var actualPercent = 0.0
    var actualCurrent = 0.0
    var actualVoltage = 0.0
    var hallEffectTriggered = true
    var pHallEffectTriggered = false
    var nominalZero = 0

    val positionTicks get() = actualPositionTicks- nominalZero

    var setpointLevel = 0
    var setpointType = HatchCargo.Hatch

//    val coolSetpoint
//        get() = LiftConstants.kHomeHeightInches + setpointLevel * FieldConstants.centerToCenterInches +
//                        when (setpointType) {
//                            HatchCargo.Hatch -> 0.0
//                            HatchCargo.Cargo -> FieldConstants.hatchToCargoHeight+0.5
//                        }

    val coolSetpoint: Double
        get() = when (setpointLevel) {
            0 -> when (setpointType) {
                HatchCargo.Hatch -> kHomeHeightInches
                HatchCargo.Cargo -> firstCargoBayCenterHeightInches
            }
            1 -> when (setpointType) {
                HatchCargo.Hatch -> secondHatchPortCenterHeightInches
                HatchCargo.Cargo -> secondCargoBayCenterHeightInches
            }
            2 -> when (setpointType) {
                HatchCargo.Hatch -> thirdHatchPortCenterHeightInches
                HatchCargo.Cargo -> thirdCargoBayCenterHeightInches
            }
            else -> kHomeHeightInches
        }

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
        actualPositionTicks = master.selectedSensorPosition
        velocityTicks = master.selectedSensorVelocity
        actualPercent = master.motorOutputPercent
//        actualCurrent = master.outputCurrent
//        actualVoltage = master.busVoltage * actualPercent
        hallEffectTriggered = !hallEffect.get()

        if (hallEffectTriggered!=pHallEffectTriggered) {
            nominalZero = actualPositionTicks
        }
        pHallEffectTriggered = hallEffectTriggered

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