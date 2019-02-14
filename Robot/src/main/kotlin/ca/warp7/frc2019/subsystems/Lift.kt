package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.lift.LiftMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.DigitalInput

@Suppress("MemberVisibilityCanBePrivate")
object Lift : Subsystem() {

    private val master = TalonSRX(LiftConstants.kMaster).apply {
        setNeutralMode(NeutralMode.Brake)
        configAllSettings(LiftConstants.kMasterTalonConfig)
        enableVoltageCompensation(true)
        enableCurrentLimit(false)
    }

    init {
        val victor = VictorSPX(LiftConstants.kFollower)
        victor.setNeutralMode(NeutralMode.Coast)
        victor.inverted = true
        victor.follow(master)
    }

    private val hallEffect = DigitalInput(LiftConstants.kHallEffect)

    enum class OutputType {
        Percent, Position, Velocity
    }

    var demand = 0.0
    var feedForward = 0.0
    var positionTicks = 0
    var velocityTicksPer100ms = 0
    var actualPercent = 0.0
    var actualCurrent = 0.0
    var actualVoltage = 0.0
    var hallEffectTriggered = true

    var outputType = OutputType.Percent
        set(value) {
            if (field != value) when (value) {
                OutputType.Percent -> Unit
                OutputType.Position -> master.selectProfileSlot(0, 0)
                OutputType.Velocity -> master.selectProfileSlot(1, 0)
            }
            field = value
        }

    override fun onDisabled() {
        master.neutralOutput()
    }

    override fun onOutput() = when (outputType) {
        OutputType.Percent -> master.set(ControlMode.PercentOutput, -demand)
        OutputType.Position -> master.set(ControlMode.Position, -demand, DemandType.ArbitraryFeedForward, feedForward)
        OutputType.Velocity -> master.set(ControlMode.Velocity, -demand, DemandType.ArbitraryFeedForward, feedForward)
    }

    override fun onMeasure(dt: Double) {
        positionTicks = master.selectedSensorPosition
        velocityTicksPer100ms = master.selectedSensorVelocity
        actualPercent = master.motorOutputPercent
        actualCurrent = master.outputCurrent
        actualVoltage = master.busVoltage * actualPercent
        hallEffectTriggered = hallEffect.get()
        LiftMotionPlanner.updateMeasurements(dt)
    }

    override fun onPostUpdate() = shuffleboard {
        add("Output Type", outputType.name)
        add("Actual Percent", actualPercent)
        add("Actual Current", actualCurrent)
        add("Actual Voltage", actualVoltage)
        add("Demand", demand)
        add("Feedforward", feedForward)
        add("Height (in)", LiftMotionPlanner.height)
        add("Velocity (in/s)", LiftMotionPlanner.velocity)
        add("Acceleration (in/s^2)", LiftMotionPlanner.acceleration)
        add(hallEffect)
    }
}