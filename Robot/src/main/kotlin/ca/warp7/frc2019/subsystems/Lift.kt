package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.LiftConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object Lift : Subsystem() {

    enum class OutputType{
        Percent, Position, Velocity, LinearPID
    }

    var percentOutput = 0.0


    var outputMode: OutputType = OutputType.Percent

    private val master = TalonSRX(LiftConstants.kMaster)

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
                TODO()
            }
            OutputType.Position ->{
                TODO()
            }
            OutputType.Velocity ->{
                TODO()
            }
        }

    }
}