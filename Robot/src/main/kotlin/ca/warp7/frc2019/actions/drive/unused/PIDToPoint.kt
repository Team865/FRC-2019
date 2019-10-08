package ca.warp7.frc2019.actions.drive.unused

import ca.warp7.frc.action.Action
import ca.warp7.frc.control.PIDControl
import ca.warp7.frc.geometry.*
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.absoluteValue
import kotlin.math.sign

class PIDToPoint(
        val target: Pose2D,
        val absolute: Boolean = true,
        val fastTurn: Boolean = false,
        val maxTurn: Double = 90.0,
        val lateralKp: Double = 25.0,
        val turningOutputKp: Double = 0.1,
        val straightPID: PIDControl = DriveConstants.kStraightPID,
        val turnPID: PIDControl = DriveConstants.kTurnPID
) : Action {

    private val io: BaseIO = ioInstance()

    private val robotState get() = Drive.robotState
    private var absoluteTarget = target

    override fun firstCycle() {
        io.driveControlMode = ControlMode.Velocity
        // calculate the target relative to the robot's starting point
        absoluteTarget = if (absolute) target else robotState + target
    }

    override val shouldFinish: Boolean
        get() = false//straightPID.isDone() && turnPID.isDone()

    override fun update() {

        // assign dt values
        straightPID.dt = io.dt
        turnPID.dt = io.dt

        // calculate the error from the robot to the target (in the perspective of the robot)
        val error = Pose2D((absoluteTarget.translation - robotState.translation).rotate(robotState.rotation),
                (absoluteTarget.rotation - robotState.rotation))

        // calculate the forward PID output with the x (forward) direction error
        var forwardOutput = straightPID.updateByError(error.translation.x)

        // reverse the lateral direction if the setpoint is behind the robot
        val lateralError = error.translation.y * error.translation.x.sign

        // calculate an offset to the angular setpoint based on the y (lateral) direction error,
        // then limiting it based on a maximum lateral turning angle. The result is in degrees
        val lateralOffset = (lateralError * lateralKp).coerceIn(-maxTurn, maxTurn)

        // calculate the total turning error accounting for the lateral offset and rotations
        // over a full circle by converting to a Rotation2D and back to degrees
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
        println("Error $error")
        println("turn $turningError")
        println("late $lateralError")
        println("lato $lateralOffset")

        // convert to ticks/100ms and set the motor velocities
        io.leftDemand = (forwardOutput - turningOutput) * DriveConstants.kTicksPerFootPer100ms
        io.rightDemand = (forwardOutput + turningOutput) * DriveConstants.kTicksPerFootPer100ms
    }

    override fun stop() {
        io.leftDemand = 0.0
        io.rightDemand = 0.0
    }
}