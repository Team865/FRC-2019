package ca.warp7.frc2019

import ca.warp7.frc2019.actions.MainLoop
import ca.warp7.frc2019.actions.Sandstorm
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.TimedRobot

class Astro : TimedRobot(0.2) {

    private val io: BaseIO = ioInstance()
    private val looper: Looper = Looper

    private val notifier: Notifier = Notifier {
        io.readInputs()
        looper.update()
        io.writeOutputs()
    }

    override fun robotInit() {
        println("Hello me is robit!")
        io.initialize()
        notifier.startPeriodic(0.01)
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

    override fun disabledPeriodic() {
    }

    override fun autonomousPeriodic() {
    }

    override fun teleopPeriodic() {
    }

    override fun robotPeriodic() {
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            RobotBase.startRobot { Astro() }
        }
    }
}