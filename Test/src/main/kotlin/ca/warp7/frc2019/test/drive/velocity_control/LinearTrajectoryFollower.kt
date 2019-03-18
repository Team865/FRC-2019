package ca.warp7.frc2019.test.drive.velocity_control

import ca.warp7.actionkt.Action
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.radians
import ca.warp7.frc.geometry.rotate
import ca.warp7.frc.interpolate
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer

@Suppress("MemberVisibilityCanBePrivate")
class LinearTrajectoryFollower : Action {
    val trajectory = LinearTrajectory(8.0)
    val moments = trajectory.moments
    val totalTime = moments.last().t
    var t = 0.0
    var i = 0
    var startTime = 0.0
    var lastTime = 0.0
    var lastYaw: Rotation2D = Rotation2D.identity

    override fun start() {
        startTime = Timer.getFPGATimestamp()
        lastYaw = Infrastructure.yaw
    }

    override fun update() {
        val nt = Timer.getFPGATimestamp()
        lastTime = nt
        t = nt - startTime
        while (i < moments.size - 3 && moments[i].t < t) i++
        val mi = moments[i]
        val mj = moments[i + 1]
        val n = (t - mi.t) / (mj.t - mi.t)

        val v = interpolate(mi.v.velocity, mj.v.velocity, n)
        val velocityGain = (v / 0.0254 * DriveConstants.kTicksPerInch) / 10

        val a = interpolate(mi.v.acceleration, mj.v.acceleration, n)
        val kA = 1.0 / 23
        val accelerationGain = (a / 0.0254 * DriveConstants.kTicksPerInch) * kA

        val newYaw = Infrastructure.yaw
        val angularKp = 20000.0
        val angularGain = newYaw.rotate(by = lastYaw.inverse).radians * angularKp
        Drive.put("angularGain", angularGain)
        lastYaw = newYaw

        Drive.controlMode = ControlMode.Velocity
        Drive.leftDemand = velocityGain + accelerationGain - angularGain
        Drive.rightDemand = velocityGain + accelerationGain + angularGain
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