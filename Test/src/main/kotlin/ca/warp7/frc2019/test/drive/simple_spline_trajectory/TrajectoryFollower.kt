package ca.warp7.frc2019.test.drive.simple_spline_trajectory

import ca.warp7.actionkt.Action
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.radians
import ca.warp7.frc.interpolate
import ca.warp7.frc.path.QuinticSegment2D
import ca.warp7.frc.trajectory.ContinuousSplineTrajectory
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer

@Suppress("MemberVisibilityCanBePrivate")
class TrajectoryFollower : Action {
    val trajectory = ContinuousSplineTrajectory(
            QuinticSegment2D(
                    0.0, 0.0, 0.0,
                    0.0, 1.0, 0.0,
                    0.0, 1.0, 0.0,
                    0.0, 0.0, 0.0),
            DriveMotionPlanner.model
    )
    val moments = trajectory.moments
    val totalTime = moments.last().t
    var t = 0.0
    var i = 0
    var startTime = 0.0
    var lastTime = 0.0
    var initYaw: Rotation2D = Rotation2D.identity

    override fun start() {
        startTime = Timer.getFPGATimestamp()
        initYaw = Infrastructure.yaw
    }

    override fun update() {
        val nt = Timer.getFPGATimestamp()
        lastTime = nt
        t = nt - startTime
        while (i < moments.size - 3 && moments[i].t < t) i++
        val mi = moments[i]
        val mj = moments[i + 1]
        val n = (t - mi.t) / (mj.t - mi.t)

        val vl = interpolate(mi.v.leftVelocity, mj.v.leftVelocity, n)
        val vr = interpolate(mi.v.rightVelocity, mj.v.rightVelocity, n)

        val leftVelocityGain = (vl / 0.0254 * DriveConstants.kTicksPerInch) / 10
        val rightVelocityGain = (vr / 0.0254 * DriveConstants.kTicksPerInch) / 10

        val al = interpolate(mi.v.leftAcceleration, mj.v.leftAcceleration, n)
        val ar = interpolate(mi.v.rightAcceleration, mj.v.rightAcceleration, n)

        val kA = 1.0 / 23

        val leftAccelerationGain = (al / 0.0254 * DriveConstants.kTicksPerInch) * kA
        val rightAccelerationGain = (ar / 0.0254 * DriveConstants.kTicksPerInch) * kA

        val expectedYaw = mi.v.state.rotation
        val actualYaw = Infrastructure.yaw - initYaw
        val angularKp = 20000.0
        val angularGain = angularKp * (expectedYaw - actualYaw).radians
        Drive.put("angularGain", angularGain)

        Drive.controlMode = ControlMode.Velocity
        Drive.leftDemand = leftVelocityGain + leftAccelerationGain - angularGain
        Drive.rightDemand = rightVelocityGain + rightAccelerationGain + angularGain
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