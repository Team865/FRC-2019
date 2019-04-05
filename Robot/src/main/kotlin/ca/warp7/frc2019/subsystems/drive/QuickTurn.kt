package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.degrees
import ca.warp7.frc.geometry.fromDegrees
import ca.warp7.frc.geometry.radians
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner.robotState
import ca.warp7.frc2019.subsystems.drive.unused.PID
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.sign
import kotlin.math.withSign

class QuickTurn(angleInDegrees: Double, val stopAngleThreshold: Double = 5.0) : Action {
    val turnPID = PID(
            kP = 2.0, kI = 0.08, kD = 5.0, kF = 0.0,
            errorEpsilon = 2.0, dErrorEpsilon = 1.0, minTimeInEpsilon = 0.3
    )
    var initYaw: Double = 0.0

    override fun start() {
        Drive.controlMode = ControlMode.Velocity
        initYaw = Infrastructure.yaw.degrees
    }

    private val angularKp = 0.029
    private val angularKd = 0.0077
    private val angularKi = 0.0001
    private val kA = 0.0007
    private val integralZone = 10.0

    override fun update() {
        val error = robotState.rotation.degrees - initYaw
        val angularGain = turnPID.updateByError(error)

        var demand = angularGain

        Drive.leftDemand = demand
        Drive.rightDemand = -demand
    }

    override val shouldFinish
        get() =turnPID.isDone()


    override fun stop() {
        Drive.apply {
            leftDemand = 0.0
            rightDemand = 0.0
            leftFeedforward = 0.0
            rightFeedforward = 0.0
        }
    }
}
