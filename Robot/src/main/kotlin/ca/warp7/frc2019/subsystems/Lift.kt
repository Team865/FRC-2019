package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.subsystems.lift.planner.LiftMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

object Lift : Subsystem() {

    private val master = TalonSRX(LiftConstants.kMaster).also {
        it.setNeutralMode(NeutralMode.Brake)
        it.configAllSettings(LiftConstants.kMasterTalonConfiguration)
        it.enableVoltageCompensation(false)
        it.enableCurrentLimit(false)
        VictorSPX(LiftConstants.kFollower).follow(it)
    }

    private val hallEffect = DigitalInput(LiftConstants.kHallEffect)

    enum class OutputType {
        PercentOutput,
        Position,
        Velocity
    }

    var demand = 0.0
    var feedForward = 0.0
    var positionTicks = 0
    var velocityTicksPer100ms = 0
    var outputPercent = 0.0
    var hallEffectTriggered = false

    var outputType = OutputType.PercentOutput
        set(value) {
            when (value) {
                OutputType.PercentOutput -> Unit
                OutputType.Position -> master.selectProfileSlot(0, 0)
                OutputType.Velocity -> master.selectProfileSlot(1, 0)
            }
            field = value
        }

    override fun onDisabled() {
        master.neutralOutput()
    }

    override fun onOutput() = when (outputType) {
        OutputType.PercentOutput -> master.set(ControlMode.PercentOutput, demand)
        OutputType.Position -> master.set(ControlMode.Position, demand, DemandType.ArbitraryFeedForward, feedForward)
        OutputType.Velocity -> master.set(ControlMode.Velocity, demand, DemandType.ArbitraryFeedForward, feedForward)
    }

    override fun onMeasure(dt: Double) {
        positionTicks = master.selectedSensorPosition
        velocityTicksPer100ms = master.selectedSensorVelocity
        outputPercent = master.motorOutputPercent
        hallEffectTriggered = hallEffect.get()
    }

    override fun onUpdateShuffleboard(container: ShuffleboardContainer) {
        container.apply {
            add("OutputType", outputType.name)
            add("Output Percent", outputPercent)
            add("Demand", demand)
            add("Feedforward", feedForward)
            add("Height (in)", LiftMotionPlanner.positionInches)
            add("Velocity (in/s)", LiftMotionPlanner.velocityInchesPerSecond)
            add(hallEffect)
        }
    }
}