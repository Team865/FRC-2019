package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.constants.LiftConstants.kMaxVelocityInchesPerSecond
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX

object Lift : Subsystem() {

    enum class OutputType{
        Percent, Position, Velocity, LinearPID
    }

    var percentOutput = 0.0
    var demandedPosition = 0.0

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
                master.set(ControlMode.Position, demandedPosition)
            }
            OutputType.Position ->{
                //TODO learn how to use Motion Magic
                //master.set(ControlMode.MotionMagic, demandedPosition, kMaxAcceleration, kMaxVelocityInchesPerSecond)
            }
            OutputType.Velocity ->{
                TODO()
            }
        }

    }
}