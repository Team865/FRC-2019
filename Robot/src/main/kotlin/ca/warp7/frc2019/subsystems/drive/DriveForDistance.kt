package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.drive.LinearTrajectoryState
import ca.warp7.frc.feetToMeters
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.Translation2D
import ca.warp7.frc.geometry.radians
import ca.warp7.frc.interpolate
import ca.warp7.frc.trajectory.LinearTrajectory
import ca.warp7.frc.trajectory.Moment
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer

class DriveForDistance(
        distanceInFeet: Double,
        val isBackwards: Boolean = distanceInFeet < 0,
        private val velocityScale: Double = 1.0,
        private val linearKp: Double = 0.0,
        private val angularKp: Double = 500.0,
        private val kA: Double = 1.0 / 30
) : Action {

    private val trajectory = LinearTrajectory(feetToMeters(distanceInFeet), DriveMotionPlanner.model)

    private val moments: List<Moment<LinearTrajectoryState<Translation2D>>> =
            if (velocityScale == 1.0) trajectory.moments
            else trajectory.moments.map { Moment(it.t / velocityScale, it.v) }

    private val totalTime = moments.last().t
    private var t = 0.0
    var i = 0
    var startTime = 0.0
    var lastTime = 0.0
    private var lastYaw: Rotation2D = Rotation2D.identity

    private val trajectorySign = if (isBackwards) -1.0 else 1.0

    override fun start() {
        startTime = Timer.getFPGATimestamp()
        lastYaw = Infrastructure.yaw
    }

    override fun update() {
        // lookup position based on time
        val nt = Timer.getFPGATimestamp()
        lastTime = nt
        t = nt - startTime
        while (i < moments.size - 3 && moments[i + 1].t < t) i++
        val thisMoment = moments[i]
        val nextMoment = moments[i + 1]

        // interpolating value between [0, 1]
        val tx = (t - thisMoment.t) / (nextMoment.t - thisMoment.t)

        // calculate velocity feedforward gain
        val expectedVelocity = interpolate(thisMoment.v.velocity, nextMoment.v.velocity, tx)
        val velocityGain = (expectedVelocity / 0.0254 * DriveConstants.kTicksPerInch) / 10

        // calculate acceleration feedforward gain
        val expectedAcceleration = interpolate(thisMoment.v.acceleration, nextMoment.v.acceleration, tx)
        val accelerationGain = (expectedAcceleration / 0.0254 * DriveConstants.kTicksPerInch) * kA

        // calculate linear feedback gain
        val expectedPosition = thisMoment.v.state.interpolate(nextMoment.v.state, tx).x
        val avg = (Drive.leftPosition + Drive.rightPosition) / 2
        val error = expectedPosition - avg / DriveConstants.kTicksPerInch * 0.0254
        val positionGain = linearKp * error

        // calculate angular feedback gain
        val newYaw = Infrastructure.yaw
        val angularGain = angularKp * (newYaw - lastYaw).radians / DriveMotionPlanner.dt
        Drive.put("angularGain", angularGain)
        lastYaw = newYaw

        // add up gains
        val pvaOutput = (velocityGain + accelerationGain + positionGain) * trajectorySign

        // apply max power constraints and set demand
        Drive.controlMode = ControlMode.Velocity
        Drive.leftDemand = pvaOutput * velocityScale - angularGain
        Drive.rightDemand = pvaOutput * velocityScale + angularGain
    }

    override val shouldFinish: Boolean get() = (t > totalTime || i >= moments.size)

    override fun stop() {
        Drive.apply {
            leftDemand = 0.0
            rightDemand = 0.0
            leftFeedforward = 0.0
            rightFeedforward = 0.0
        }
    }
}