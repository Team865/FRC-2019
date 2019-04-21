package ca.warp7.frc2019.subsystems.drive.unused

import ca.warp7.actionkt.Action
import ca.warp7.frc.PID
import ca.warp7.frc.drive.ChassisState
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.degrees
import ca.warp7.frc.geometry.rotate
import ca.warp7.frc.interpolate
import ca.warp7.frc.kFeetToMeters
import ca.warp7.frc.path.parameterizedSplinesOf
import ca.warp7.frc.trajectory.timedTrajectory
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.v2.subsystems.Drive
import edu.wpi.first.wpilibj.Timer

class PIDTrajectory(
        waypoints: Array<Pose2D>,
        maxVelocityRatio: Double = 0.75,
        maxAccelerationRatio: Double = 0.85,
        val backwards: Boolean = false,
        val lookaheadDist: Double = 0.2, // m
        val kA: Double = 0.2, // unit: maxAcceleration(m/s) / 1023
        val lateralKp: Double = 1.0,
        val maxFeedbackTurn: Double = 90.0, // deg
        val startVelocity: Double = 0.0, // ft/s
        val endVelocity: Double = 0.0, // ft/s
        val enableFeedforward: Boolean = true,
        val enableFeedback: Boolean = true,
        val straightPID: PID = PID(
                kP = 0.15, kI = 0.0015, kD = 0.3, kF = 0.0,
                errorEpsilon = 0.25, dErrorEpsilon = 0.2, minTimeInEpsilon = 0.3
        ),
        val turnPID: PID = PID(
                kP = 3.5, kI = 0.08, kD = 5.0, kF = 0.0,
                errorEpsilon = 2.0, dErrorEpsilon = 1.0, minTimeInEpsilon = 0.3
        )
) : Action {

    private val io: RobotIO = RobotIO

    private val robotState get() = Drive.robotState
    private val model = Drive.model

    val trajectory = parameterizedSplinesOf(*waypoints).timedTrajectory(
            model = model,
            startVelocity = kFeetToMeters * startVelocity,
            endVelocity = kFeetToMeters * endVelocity,
            maxVelocity = model.maxVelocity * maxVelocityRatio,
            maxAcceleration = model.maxAcceleration * maxAccelerationRatio
    )

    val trajectoryTime = trajectory.last().t

    var startTime = 0.0
    var t = 0.0
    var i = 0

    override fun start() {
        i = 0
        startTime = Timer.getFPGATimestamp()
        io.drivePID = DriveConstants.kVelocityFeedforwardPID
        Drive.setVelocity(startVelocity, startVelocity)
    }

    override val shouldFinish: Boolean
        get() = t > trajectoryTime || i >= trajectory.size

    override fun stop() {
        Drive.setVelocity(endVelocity, endVelocity)
    }

    override fun update() {
        val nt = Timer.getFPGATimestamp()
        t = nt - startTime

        while (i < trajectory.size - 2 && trajectory[i + 1].t < t) i++

        val thisMoment = trajectory[i]
        val nextMoment = trajectory[i + 1]
        val tx = (t - thisMoment.t) / (nextMoment.t - thisMoment.t)

        var leftVel = 0.0
        var rightVel = 0.0
        var leftAcc = 0.0
        var rightAcc = 0.0

        if (enableFeedforward) {

            val curvature = interpolate(thisMoment.state.curvature, nextMoment.state.curvature, tx)

            val linearVel = interpolate(thisMoment.velocity, nextMoment.velocity, tx)
            val angularVel = linearVel * curvature
            val velocity = model.solve(ChassisState(linearVel, angularVel))

            val linearAcc = interpolate(thisMoment.acceleration, nextMoment.acceleration, tx)
            val angularAcc = linearAcc * curvature
            val acceleration = model.solve(ChassisState(linearAcc, angularAcc))

            if (backwards) {
                leftVel = -velocity.right
                rightVel = -velocity.left
                leftAcc = -acceleration.right * kA
                rightAcc = -acceleration.left * kA
            } else {
                leftVel = velocity.left
                rightVel = velocity.right
                leftAcc = acceleration.left * kA
                rightAcc = acceleration.right * kA
            }
        }

        if (true) {

            val currentPos = thisMoment.state.state.translation.interpolate(nextMoment.state.state.translation, tx)
            var trackingDist = (nextMoment.state.state.translation - currentPos).mag
            var j = i + 1

            while (j < trajectory.size - 2 && trackingDist < lookaheadDist) {
                trackingDist += (trajectory[j + 1].state.state.translation - trajectory[j].state.state.translation).mag
                j++
            }

            val lookahead = trajectory[j]

            straightPID.dt = io.dt
            turnPID.dt = io.dt

            val target = lookahead.state.state

            val error = Pose2D((target.translation - robotState.translation).rotate(robotState.rotation),
                    (target.rotation - robotState.rotation))

            println("Target: $target")
            println("RobotState: $robotState")
            println("Error: $error")
            println()

            if (enableFeedback) {
//
//                val forwardFeedback = straightPID.updateByError(error.translation.x)
//
//                val lateralError = error.translation.y * error.translation.x.sign
//                val lateralOffset = (lateralError * lateralKp).coerceIn(-maxFeedbackTurn, maxFeedbackTurn)

                val turningError = (error.rotation/* - Rotation2D.fromDegrees(lateralOffset)*/).degrees
                val turningFeedback = turnPID.updateByError(turningError)

                // println("$forwardFeedback, $turningFeedback")

                leftVel +=/* forwardFeedback -*/ turningFeedback
                rightVel += /*forwardFeedback +*/ turningFeedback
            }
        }

        Drive.setVelocity(leftVel, rightVel, leftAcc, rightAcc)
    }
}