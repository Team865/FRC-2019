package ca.warp7.frc2019.test.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.geometry.radians
import ca.warp7.frc2019.Looper
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import kotlin.math.PI

class WheelbaseRadius : TimedRobot(0.2) {

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
        looper.add(Test)
    }

    private object Test : Action {
        private val io: BaseIO = ioInstance()

        var accumulator = 0.0
        var startDist = 0.0

        override fun start() {
            accumulator = 0.0

            startDist = (io.leftPosition + io.rightPosition) * DriveConstants.kWheelRadius / 2
            io.driveControlMode = ControlMode.PercentOutput
        }

        override fun update() {
            io.leftDemand = 0.4
            io.rightDemand = -0.4
            accumulator += (io.yaw - io.previousYaw).radians
            val newDist = (io.leftPosition + io.rightPosition) * DriveConstants.kWheelRadius / 2
            val dist = newDist - startDist
            if (accumulator > 10 * 2 * PI) {
                io.leftDemand = 0.0
                io.rightDemand = 0.0
                startDist = newDist
                val r = dist / (10 * 2 * PI)
                SmartDashboard.putNumber("Wheelbase", r)
                SmartDashboard.putNumber("ScrubFactor", r / (DriveConstants.kTurningDiameter / 2))
            }
            SmartDashboard.putNumber("WheelbaseNow", dist / accumulator)
            SmartDashboard.putNumber("ScrubNow", (dist / accumulator) / (DriveConstants.kTurningDiameter / 2))
            // 0.62865
            // 0.07493 wheelRadius
        }

        override val shouldFinish: Boolean
            get() = false
    }


    override fun disabledPeriodic() {}
    override fun autonomousPeriodic() {}
    override fun teleopPeriodic() {}
    override fun robotPeriodic() {}

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            RobotBase.startRobot { WheelbaseRadius() }
        }
    }
}