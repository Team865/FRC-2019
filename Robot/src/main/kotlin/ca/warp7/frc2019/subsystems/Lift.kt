package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.LiftConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

object Lift : Subsystem() {

    enum class OutputType{
        Percent, Velocity, LinearPID, Hold
    }

    var percentOutput = 0.0
    var demandedHeightFromHome = 0.0
    var positionFromHome = 0.0
    var velocity = 0.0
    var demandedVelocity = 0.0

    var outputMode: OutputType = OutputType.Percent

    private val master = TalonSRX(LiftConstants.kMaster)
    private val hallEffect = DigitalInput(LiftConstants.kHallEffect)

    init {
        VictorSPX(LiftConstants.kFollower).follow(master)
    }

    override fun onDisabled() {
        master.neutralOutput()
    }

    override fun onOutput() {
        when (outputMode){
            OutputType.Percent -> {
                master.set(ControlMode.PercentOutput, percentOutput)
            }
            OutputType.LinearPID ->{
                master.set(ControlMode.Position, demandedHeightFromHome)
            }
            OutputType.Velocity ->{
                master.set(ControlMode.Velocity, demandedVelocity)
            }
            OutputType.Hold ->
            {
                master.setNeutralMode(NeutralMode.Brake)
            }
        }

    }

    override fun onMeasure(dt: Double) {
        positionFromHome = master.selectedSensorPosition / LiftConstants.kInchesPerTick
        velocity = master.selectedSensorVelocity / LiftConstants.kInchesPerTick
    }

    override fun onUpdateShuffleboard(container: ShuffleboardContainer) {
        container.add(hallEffect)
    }
}