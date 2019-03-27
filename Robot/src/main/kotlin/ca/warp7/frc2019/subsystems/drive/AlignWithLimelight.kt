package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Limelight
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer
import kotlin.math.withSign

class AlignWithLimelight : Action {
    var left = 0.0
    var right = 0.0

    private var lastError = 0.0
    private var lastTime = 0.0

    override fun start() {
        Drive.controlMode = ControlMode.PercentOutput
    }

    override fun update() {
        if (Limelight.hasTarget) {
            val error = Math.toRadians(Limelight.x)
            val time = Timer.getFPGATimestamp()
            val dt = time - lastTime
            val dError = error - lastError

            val kP = 0.01
            val kD = 2.0 / (Limelight.area)
            val kVi: Double = 0.2

            left = 0.4
            right = 0.4
            val friction = kVi.withSign(error)
            Drive.leftDemand = left + (error * kP + dError / dt * kD + friction)
            Drive.rightDemand = right - (error * kP + dError / dt * kD + friction)
            println((Drive.leftVelocity + Drive.rightVelocity) / 2.0)

            lastError = error
            lastTime = time
        }
    }

    override val shouldFinish get() = Limelight.x <= 0.5 && lastError <= 0.5

    override fun stop() {
        Drive.controlMode = ControlMode.PercentOutput
        Drive.leftDemand = 0.0
        Drive.rightDemand = 0.0
    }
}