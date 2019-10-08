package ca.warp7.frc2019.actions.drive.unused

import ca.warp7.frc.action.Action
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.fromDegrees
import ca.warp7.frc.geometry.radians
import ca.warp7.frc.kFeetToMetres
import ca.warp7.frc.linearInterpolate
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.lib.LinearTrajectory
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode

class DriveForDistanceLimelight(distanceInFeet: Double) : Action {
    private val io: BaseIO = ioInstance()

    val trajectory = LinearTrajectory(kFeetToMetres * distanceInFeet, Drive.model)
    val moments = trajectory.moments
    val totalTime = moments.last().t
    var t = 0.0
    var i = 0
    var startTime = 0.0
    var lastTime = 0.0
    var lastYaw: Rotation2D = Rotation2D.identity

    override fun start() {
        startTime = io.time
        lastYaw = io.yaw
    }

    override fun update() {
        val nt = io.time
        lastTime = nt
        t = nt - startTime
        while (i < moments.size - 3 && moments[i].t < t) i++
        val mi = moments[i]
        val mj = moments[i + 1]
        val n = (t - mi.t) / (mj.t - mi.t)

        val v = linearInterpolate(mi.v.velocity, mj.v.velocity, n)
        val velocityGain = (v / 0.0254 * DriveConstants.kTicksPerInch) / 10

        val a = linearInterpolate(mi.v.acceleration, mj.v.acceleration, n)
        val kA = 1.0 / 23
        val accelerationGain = (a / 0.0254 * DriveConstants.kTicksPerInch) * kA

        val newYaw = Rotation2D.fromDegrees(io.visionErrorX)
        val angularKp = 400.0
        val angularGain = angularKp * (newYaw - lastYaw).radians / io.dt
        lastYaw = newYaw

        io.driveControlMode = ControlMode.Velocity
        io.leftDemand = velocityGain + accelerationGain - angularGain
        io.rightDemand = velocityGain + accelerationGain + angularGain
    }

    override val shouldFinish: Boolean
        get() = (t > totalTime || i >= moments.size) //&&
    //(Drive.leftVelocity.absoluteValue + Drive.rightVelocity.absoluteValue) / 2 <= stopVelThreshold

    override fun stop() {
        io.apply {
            leftDemand = 0.0
            rightDemand = 0.0
            leftFeedforward = 0.0
            rightFeedforward = 0.0
        }
    }
}