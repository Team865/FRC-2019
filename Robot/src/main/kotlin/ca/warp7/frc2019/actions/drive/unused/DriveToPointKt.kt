package ca.warp7.frc2019.actions.drive.unused


import ca.warp7.actionkt.Action
import ca.warp7.frc.PID
import ca.warp7.frc.geometry.*
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode

class DriveToPointKt(
        private val x: Double,
        private val y: Double,
        private val theta: Double,
        private val minVelocity: Double = 0.0,
        private val maxVelocity: Double = 10.0,
        private val eps: Double = 0.25,
        private val turnRate: Double = -1.0,
        private val maxTurn: Double = 90.0,
        private val slowTurn: Boolean = true
) : Action {

    private val io: BaseIO = ioInstance()

    private val turnPID = PID(3.5, 0.08, 5.0, 0.25,
            0.0, 0.0, 0.0, 0.0, 0.0)
    private val straightPID = PID(0.15, 0.0015, 0.3, 0.25,
            0.0, 0.0, 0.0, 0.0, 0.0)

    // rotates the xy coordinates to be relative to the angle of the target
    val rotatedError: Translation2D
        get() {
            val currentX = Drive.robotState.translation.x
            val currentY = Drive.robotState.translation.y
            val rotation = 90 - this.theta

            val currentPosition = Translation2D(currentX, currentY)
            val finalPosition = Translation2D(this.x, this.y)

            currentPosition.rotate(Rotation2D.fromDegrees(rotation))
            finalPosition.rotate(Rotation2D.fromDegrees(rotation))

            val xError = finalPosition.x - currentPosition.x
            val yError = finalPosition.y - currentPosition.y

            return Translation2D(xError, yError)
        }

    var done = false

    override fun update() {
        var error = rotatedError
        val targetHeading: Double
        // flip X if we are going backwards
        if (error.y < 0) error = Translation2D(-error.x, error.y)
        // based on how far we are in x turn more
        var turningOffset = error.x * this.turnRate
        // limit it to be within 90* from the target angle
        turningOffset = turningOffset.coerceIn(-maxTurn, maxTurn)
        targetHeading = this.theta - turningOffset

        val angle = Drive.robotState.rotation.degrees
        val offset = angle % 360

        // Corrects the target to work with Gyro position
        when {
            targetHeading - offset < -180 -> turnPID.setpoint = angle + 360.0 + targetHeading - offset
            targetHeading - offset < 180 -> turnPID.setpoint = angle + targetHeading - offset
            else -> turnPID.setpoint = angle - 360 + targetHeading - offset
        }

        val yError = error.y
        straightPID.dt = io.dt

        var yOutput: Double
        yOutput = straightPID.updateByError(yError)

        var distanceFromTargetHeading = Math.abs(turnPID.setpoint - Drive.robotState.rotation.degrees)
        // prevents the y output from being reversed in the next calculation
        if (distanceFromTargetHeading > 90) distanceFromTargetHeading = 90.0

        // slow down y if we aren't facing the correct angle
        if (slowTurn) yOutput *= (-1 * distanceFromTargetHeading / 90.0 + 1)

        var xOutput = -turnPID.updateBySetpoint(Drive.robotState.rotation.degrees)

        if (!this.slowTurn) {
            xOutput *= 0.85
        }

        val leftOut = yOutput + xOutput
        val rightOut = yOutput - xOutput

        io.driveControlMode = ControlMode.Velocity
        io.leftDemand = leftOut
        io.rightDemand = rightOut

        done = Math.abs(yError) < this.eps
    }

    override val shouldFinish: Boolean
        get() = done && minVelocity <= 0.5

    override fun stop() {
        io.apply {
            leftDemand = 0.0
            rightDemand = 0.0
            leftFeedforward = 0.0
            rightFeedforward = 0.0
        }
    }
}
