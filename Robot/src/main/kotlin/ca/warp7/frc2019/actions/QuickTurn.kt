package ca.warp7.frc2019.actions

import ca.warp7.frc.action.Action
import ca.warp7.frc.control.PID
import ca.warp7.frc.control.PIDControl
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.degrees
import ca.warp7.frc.geometry.fromDegrees
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive.robotState
import com.ctre.phoenix.motorcontrol.ControlMode

class QuickTurn(val angleInDegrees: Double, val stopAngleThreshold: Double = 5.0) : Action {
    private val io: BaseIO = ioInstance()

    val turnPID = PIDControl(
            PID(kP = 1.5, kI = 0.004, kD = 5.0, kF = 0.0),
            errorEpsilon = 2.0, dErrorEpsilon = 1.0, minTimeInEpsilon = 0.3,
            maxOutput = DriveConstants.kMaxVelocity
    )

    var initYaw: Rotation2D = Rotation2D.identity

    override fun firstCycle() {
        io.driveControlMode = ControlMode.Velocity
        initYaw = robotState.rotation
        println("ERROR start $robotState")
    }

    private val angularKp = 0.029
    private val angularKd = 0.0077
    private val angularKi = 0.0001
    private val kA = 0.0007
    private val integralZone = 10.0

    override fun update() {
        val error = robotState.rotation - initYaw - Rotation2D.fromDegrees(angleInDegrees)

        turnPID.dt = io.dt
        val angularGain = turnPID.updateByError(error.degrees)

        val demand = angularGain * DriveConstants.kTicksPerFootPer100ms
        println("ERROR $robotState")
        io.leftDemand = demand
        io.rightDemand = -demand
    }

    override fun shouldFinish(): Boolean {
        return false
    }


    override fun interrupt() {
        io.apply {
            leftDemand = 0.0
            rightDemand = 0.0
            leftFeedforward = 0.0
            rightFeedforward = 0.0
        }
    }
}
