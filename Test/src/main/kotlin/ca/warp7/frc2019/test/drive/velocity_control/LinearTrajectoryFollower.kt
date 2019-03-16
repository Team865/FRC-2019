package ca.warp7.frc2019.test.drive.velocity_control

import ca.warp7.actionkt.Action
import ca.warp7.frc.geometry.interpolate
import ca.warp7.frc.interpolate
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer

@Suppress("MemberVisibilityCanBePrivate")
class LinearTrajectoryFollower : Action {
    val trajectory = LinearTrajectory(12.0)
    val moments = trajectory.moments
    val totalTime = moments.last().t
    var t = 0.0
    var i = 0
    var startTime = 0.0
    var startYaw = 0.0
    var lastTime = 0.0
    var lastYawError = 0.0

    override fun start() {
        startTime = Timer.getFPGATimestamp()
        startYaw = Infrastructure.fusedHeading
    }

    override fun update() {
        val nt = Timer.getFPGATimestamp()
        val dt = nt - lastTime
        lastTime = nt
        t = nt - startTime
        while (i < moments.size - 3 && moments[i].t < t) i++
        val mi = moments[i]
        val mj = moments[i + 1]
        val n = (t - mi.t) / (mj.t - mi.t)
        val p = mi.v.state.interpolate(mj.v.state, n).mag
        val v = interpolate(mi.v.velocity, mj.v.velocity, n)
        val a = interpolate(mi.v.acceleration, mj.v.acceleration, n)
        val yawError = startYaw - Infrastructure.fusedHeading
        val kD = 0
        val dYawError = (lastYawError - yawError) * dt

        lastYawError = yawError
        Drive.apply {
            controlMode = ControlMode.Velocity
            val leftPos = Drive.leftPosition / DriveConstants.kTicksPerInch * 0.0254
            val rightPos = Drive.rightPosition / DriveConstants.kTicksPerInch * 0.0254
            println("$leftPos, $rightPos, $p")
            leftDemand = (v / 0.0254 * DriveConstants.kTicksPerInch) / 10 + (a / 0.0254 * DriveConstants.kTicksPerInch) / 23
            rightDemand = (v / 0.0254 * DriveConstants.kTicksPerInch) / 10 + (a / 0.0254 * DriveConstants.kTicksPerInch) / 23
            //leftFeedforward = a / trajectory.maxAcceleration
            //rightFeedforward = a / trajectory.maxAcceleration
        }
    }

    override val shouldFinish: Boolean
        get() = t > totalTime || i >= moments.size

    override fun stop() {
        Drive.apply {
            leftDemand = 0.0
            rightDemand = 0.0
            leftFeedforward = 0.0
            rightFeedforward = 0.0
        }
    }
}

fun main() {
    LinearTrajectory(10.0).moments.forEach { println(it) }
}

// m/s^2 to ticks/(0.1s)^2