package ca.warp7.frc2019

import ca.warp7.frc2019.loops.MainLoop
import ca.warp7.frc2019.loops.Sandstorm
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.TimedRobot.startRobot

class Astro : TimedRobot() {

    private val io: RobotIO by lazy { RobotIO }
    private val looper: Looper = Looper

    override fun robotInit() {
        println("Hello me is robit!")
        io.initialize()
    }

    override fun disabledInit() {
        io.disable()
        looper.reset()
    }

    override fun autonomousInit() {
        io.enable()
        looper.add(Sandstorm())
    }

    override fun teleopInit() {
        io.enable()
        looper.add(MainLoop())
    }

    override fun robotPeriodic() {
        io.readInputs()
        looper.update()
        io.writeOutputs()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = startRobot(::Astro)
    }
}