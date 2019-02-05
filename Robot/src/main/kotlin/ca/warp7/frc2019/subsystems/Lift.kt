package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.LiftConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

object Lift : Subsystem() {

    private val master = TalonSRX(LiftConstants.kMaster).also {
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
    var measuredPosition = 0.0
    var measuredVelocity = 0.0
    var outputType = OutputType.PercentOutput

    init {
        master.setNeutralMode(NeutralMode.Brake)
        VictorSPX(LiftConstants.kFollower).follow(master)
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
        measuredPosition = master.selectedSensorPosition / LiftConstants.kInchesPerTick
        measuredVelocity = master.selectedSensorVelocity / LiftConstants.kInchesPerTick
    }

    override fun onUpdateShuffleboard(container: ShuffleboardContainer) {
        container.add(hallEffect)
    }
}