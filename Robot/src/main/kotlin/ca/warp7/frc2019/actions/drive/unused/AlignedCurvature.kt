package ca.warp7.frc2019.actions.drive.unused

import ca.warp7.frc.action.Action
import ca.warp7.frc.control.PID
import ca.warp7.frc.control.PIDControl
import ca.warp7.frc.control.speedController
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import kotlin.math.withSign

class AlignedCurvature : Action {
    private val io: BaseIO = ioInstance()

    var xSpeed = 0.0
    var zRotation = 0.0
    var isQuickTurn = false
    var isAligning = false

    var left = 0.0
    var right = 0.0

    val anglePID = PIDControl(PID(kP = 0.6, kD = 0.05))

    private val differentialDrive = DifferentialDrive(speedController { left = it }, speedController { right = it })

    init {
        differentialDrive.apply {
            setDeadband(DriveConstants.kDifferentialDeadband)
            isSafetyEnabled = false
            isRightSideInverted = false
        }
    }

    override fun start() {
        io.driveControlMode = ControlMode.PercentOutput
        xSpeed = 0.0
        zRotation = 0.0
        isQuickTurn = false
    }

    override fun update() {
        // Reverse the curvature direction when drive train is going in
        // reverse or when it's quick turning
        if (xSpeed < -0.2) zRotation *= -1
        else if (isQuickTurn) zRotation *= DriveConstants.kQuickTurnMultiplier
        differentialDrive.curvatureDrive(xSpeed, zRotation, isQuickTurn)

        if (isAligning && io.foundVisionTarget) {
            val kVi: Double
            if (xSpeed.epsilonEquals(0.0, 0.2)) {
                anglePID.pid.kP = 0.6
                anglePID.pid.kD = 0.05
                kVi = 0.2
            } else {
                left = 0.4
                right = 0.4
                anglePID.pid.kP = 0.01
                anglePID.pid.kD = 2.0 / (io.visionArea)
                kVi = 0.05
            }

            anglePID.dt = io.dt
            val angleAdjustment = anglePID.updateByError(-Math.toRadians(io.visionErrorX))
            val friction = kVi.withSign(angleAdjustment)

            if (angleAdjustment >= 0) {
                io.leftDemand = left
                io.rightDemand = right + (angleAdjustment + friction)
            } else {
                io.leftDemand = left - (angleAdjustment + friction)
                io.rightDemand = right
            }
        } else {
            io.leftDemand = left
            io.rightDemand = right
        }
    }

    override val shouldFinish get() = false

    override fun stop() {
        io.driveControlMode = ControlMode.PercentOutput
        io.leftDemand = 0.0
        io.rightDemand = 0.0
    }
}