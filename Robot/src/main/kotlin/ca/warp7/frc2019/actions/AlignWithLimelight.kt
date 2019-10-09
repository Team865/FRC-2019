package ca.warp7.frc2019.actions

import ca.warp7.frc.action.Action
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.withSign

class AlignWithLimelight : Action {
    private val io: BaseIO = ioInstance()

    var left = 0.0
    var right = 0.0

    private var lastError = 0.0
    private var lastTime = 0.0

    override fun firstCycle() {
        io.driveControlMode = ControlMode.PercentOutput
    }

    override fun update() {
        if (io.foundVisionTarget) {
            val error = Math.toRadians(io.visionErrorX)
            val dt = io.dt
            val dError = error - lastError

            val kP = 1.9
            val kD = 0.95
            val kVi = 0.2

            val friction = kVi.withSign(error)
            io.leftDemand = error * kP + dError / dt * kD + friction
            io.rightDemand = right - (error * kP + dError / dt * kD + friction)

            println("error $error")
            println("demand ${error * kP + dError / dt * kD + friction}")
            //println((Drive.leftVelocity + Drive.rightVelocity) / 2.0)

            lastError = error
        }
    }

    override fun shouldFinish(): Boolean {
        return io.visionErrorX.epsilonEquals(0.0, 0.5)
                && io.angularVelocity.epsilonEquals(0.0, 0.07)
                && io.foundVisionTarget
    }

    override fun interrupt() {
        io.driveControlMode = ControlMode.PercentOutput
        io.leftDemand = 0.0
        io.rightDemand = 0.0
    }
}