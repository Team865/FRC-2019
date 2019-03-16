package ca.warp7.frc2019.test.drive.velocity_control

import ca.warp7.actionkt.Action
import ca.warp7.frc.interpolate
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer

@Suppress("MemberVisibilityCanBePrivate")
class LinearTrajectoryFollower : Action {
    val trajectory = LinearTrajectory(25.0)
    val moments = trajectory.moments
    val totalTime = moments.last().t
    var t = 0.0
    var i = 0
    var startTime = 0.0

    override fun start() {
        startTime = Timer.getFPGATimestamp()
    }

    override fun update() {
        t = Timer.getFPGATimestamp() - startTime
        while (i < moments.size - 3 && moments[i].t < t) i++
        val mi = moments[i]
        val mj = moments[i + 1]
        val n = (t - mi.t) / (mj.t - mi.t)
        val v = interpolate(mi.v.velocity, mj.v.velocity, n)
        val a = interpolate(mi.v.acceleration, mj.v.acceleration, n)
        Drive.apply {
            controlMode = ControlMode.Velocity
            leftDemand = (v * 0.0254 * DriveConstants.kTicksPerInch) / 10
            rightDemand = (v * 0.0254 * DriveConstants.kTicksPerInch) / 10
            println("$leftDemand, $rightDemand")
            leftFeedforward = a / trajectory.maxAcceleration / 1023
            rightFeedforward = a / trajectory.maxAcceleration / 1023
        }
    }

    override val shouldFinish: Boolean
        get() = t > totalTime || i >= moments.size
}