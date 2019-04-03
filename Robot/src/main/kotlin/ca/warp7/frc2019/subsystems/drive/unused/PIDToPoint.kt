package ca.warp7.frc2019.subsystems.drive.unused

import ca.warp7.actionkt.Action
import ca.warp7.frc.geometry.*
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.absoluteValue

class PIDToPoint(
        val target: Pose2D,
        val absolute: Boolean = true,
        val fastTurn: Boolean = true,
        val maxTurn: Double = 90.0,
        val lateralKp: Double = 1.0,
        val turningOutputKp: Double = 0.8,
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
    private var absoluteTarget = target

    override fun start() {
        Drive.controlMode = ControlMode.Velocity
        // calculate the target relative to the robot's starting point
        absoluteTarget = if (absolute) target else robotState + target
    }

    override val shouldFinish: Boolean
        get() = straightPID.isDone() && turnPID.isDone()

    override fun update() {

        // assign dt values
        straightPID.dt = DriveMotionPlanner.dt
        turnPID.dt = DriveMotionPlanner.dt

        // calculate the error from the robot to the target (in the perspective of the robot)
        var error = Pose2D((absoluteTarget.translation - robotState.translation).rotate(robotState.rotation),
                (absoluteTarget.rotation - robotState.rotation))

        // calculate the forward PID output with the x (forward) direction error
        var forwardOutput = straightPID.updateByError(error.translation.x)

        // reverse the lateral direction if the setpoint is behind the robot
        if (error.translation.x < 0) error = Pose2D(error.translation.flipY, error.rotation)

        // calculate an offset to the angular setpoint based on the y (lateral) direction error,
        // then limiting it based on a maximum lateral turning angle. The result is in degrees
        val lateralOffset = (error.translation.y * lateralKp).coerceIn(-maxTurn, maxTurn)

        // calculate the total turning error taking in account of the lateral offset accounts for
        // rotation over a full circle by converting to a Rotation2D and back to degrees
        val turningError = (error.rotation - Rotation2D.fromDegrees(lateralOffset)).degrees

        // calculate the turning PID output with the total turning error
        var turningOutput = turnPID.updateByError(turningError)

        if (fastTurn) {
            // find the magnitude of angular error in degrees and limit it to 90 degrees,
            // so that the forward output multiplier cannot be less than 0
            val angularError = error.rotation.degrees.absoluteValue.coerceAtMost(90.0)
            // slow down the forward output based on the angular error
            forwardOutput *= 1 - angularError / 90
        } else {
            // slow down the turning output to go straight more
            turningOutput *= turningOutputKp
        }

        // convert to ticks/100ms and set the motor velocities
        Drive.leftDemand = (forwardOutput - turningOutput) * DriveConstants.kTicksPerFootPer100ms
        Drive.rightDemand = (forwardOutput + turningOutput) * DriveConstants.kTicksPerFootPer100ms
    }

    override fun stop() {
        Drive.leftDemand = 0.0
        Drive.rightDemand = 0.0
    }
}