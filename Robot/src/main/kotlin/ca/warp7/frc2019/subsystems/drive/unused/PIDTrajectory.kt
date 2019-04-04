package ca.warp7.frc2019.subsystems.drive.unused

import ca.warp7.actionkt.Action
import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.drive.solve
import ca.warp7.frc.feetToMeters
import ca.warp7.frc.geometry.*
import ca.warp7.frc.interpolate
import ca.warp7.frc.path.parameterizedSplinesOf
import ca.warp7.frc.trajectory.timedTrajectory
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer

class PIDTrajectory(
        waypoints: Array<Pose2D>,
        maxVelocityRatio: Double = 0.75,
        maxAccelerationRatio: Double = 0.85,
        val lookAheadDistance: Double = 0.2,
        val kA: Double = 0.2,
        val lateralKp: Double = 1.0,
        val maxFeedbackTurn: Double = 90.0,
        val startVelocity: Double = 0.0,
        val endVelocity: Double = 0.0,
        val straightPID: PID = PID(
                kP = 0.15, kI = 0.0015, kD = 0.3, kF = 0.0,
                errorEpsilon = 0.25, dErrorEpsilon = 0.2, minTimeInEpsilon = 0.3
        ),
        val turnPID: PID = PID(
                kP = 3.5, kI = 0.08, kD = 5.0, kF = 0.0,
                errorEpsilon = 2.0, dErrorEpsilon = 1.0, minTimeInEpsilon = 0.3
        )
) : Action {
    private val robotState get() = DriveMotionPlanner.robotState
    private val model = DriveMotionPlanner.model

    val trajectory = parameterizedSplinesOf(*waypoints).timedTrajectory(
            model = model,
            startVelocity = feetToMeters(startVelocity),
            endVelocity = feetToMeters(endVelocity),
            maxVelocity = model.maxVelocity * maxVelocityRatio,
            maxAcceleration = model.maxAcceleration * maxAccelerationRatio
    )

    val trajectoryTime = trajectory.last().t

    var startTime = 0.0
    var t = 0.0
    var i = 0

    override fun start() {
        startTime = Timer.getFPGATimestamp()
        i = 0
        Drive.controlMode = ControlMode.Velocity
        Drive.leftDemand = startVelocity * DriveConstants.kTicksPerMeterPer100ms
        Drive.rightDemand = startVelocity * DriveConstants.kTicksPerMeterPer100ms
    }

    override val shouldFinish: Boolean
        get() = t > trajectoryTime || i >= trajectory.size

    override fun stop() {
        Drive.leftDemand = endVelocity * DriveConstants.kTicksPerMeterPer100ms
        Drive.rightDemand = endVelocity * DriveConstants.kTicksPerMeterPer100ms
    }

    override fun update() {
        val nt = Timer.getFPGATimestamp()
        t = nt - startTime
        while (i < trajectory.size - 2 && trajectory[i].t < t) i++
        val thisMoment = trajectory[i]
        val nextMoment = trajectory[i + 1]
        val tx = (t - thisMoment.t) / (nextMoment.t - thisMoment.t)

        val linear = interpolate(thisMoment.velocity, nextMoment.velocity, tx)
        val angular = linear * interpolate(thisMoment.state.curvature, nextMoment.state.curvature, tx)
        val wheelFeedforward = model.solve(ChassisState(linear, angular))
        val currentPos = thisMoment.state.state.translation.interpolate(nextMoment.state.state.translation, tx)
        var trackingDist = (nextMoment.state.state.translation - currentPos).mag

        var j = i + 1
        while (j < trajectory.size - 2 && trackingDist < lookAheadDistance) {
            trackingDist += (trajectory[j + 1].state.state.translation - trajectory[j].state.state.translation).mag
            j++
        }
        val lookahead = trajectory[j]
        straightPID.dt = DriveMotionPlanner.dt
        turnPID.dt = DriveMotionPlanner.dt
        val target = lookahead.state.state

        var error = Pose2D((target.translation - robotState.translation).rotate(robotState.rotation),
                (target.rotation - robotState.rotation))
        val forwardFeedback = straightPID.updateByError(error.translation.x)
        if (error.translation.x < 0) error = Pose2D(error.translation.flipY, error.rotation)
        val lateralOffset = (error.translation.y * lateralKp).coerceIn(-maxFeedbackTurn, maxFeedbackTurn)
        val turningError = (error.rotation - Rotation2D.fromDegrees(lateralOffset)).degrees
        val turningFeedback = turnPID.updateByError(turningError)

        val leftVel = wheelFeedforward.left + forwardFeedback - turningFeedback
        val rightVel = wheelFeedforward.right + forwardFeedback + turningFeedback

        val lookaheadVelocity = model.solve(ChassisState(lookahead.velocity,
                lookahead.velocity * lookahead.state.curvature))
        val leftAcc = (lookaheadVelocity.left - wheelFeedforward.left) / (lookahead.t - t)
        val rightAcc = (lookaheadVelocity.right - wheelFeedforward.right) / (lookahead.t - t)

        Drive.leftDemand = leftVel * DriveConstants.kTicksPerMeterPer100ms + leftAcc * kA
        Drive.rightDemand = rightVel * DriveConstants.kTicksPerMeterPer100ms + rightAcc * kA
    }
}