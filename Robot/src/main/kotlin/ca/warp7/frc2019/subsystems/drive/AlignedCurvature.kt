package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.PID
import ca.warp7.frc.control.speedController
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Limelight
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import kotlin.math.withSign

class AlignedCurvature : Action {
    var xSpeed = 0.0
    var zRotation = 0.0
    var isQuickTurn = false
    var isAligning = false

    var left = 0.0
    var right = 0.0

    val anglePID = PID(kP = 0.6, kD = 0.05)

    private val differentialDrive = DifferentialDrive(speedController { left = it }, speedController { right = it })

    init {
        differentialDrive.apply {
            setDeadband(DriveConstants.kDifferentialDeadband)
            isSafetyEnabled = false
            isRightSideInverted = false
        }
    }

    override fun start() {
        Drive.controlMode = ControlMode.PercentOutput
        xSpeed = 0.0
        zRotation = 0.0
        isQuickTurn = false
    }

    override fun update() {
        // Reverse the curvature direction when drive train is going in
        // reverse or when it's quick turning
        if (xSpeed < -ControlConstants.kControlDeadband) zRotation *= -1
        else if (isQuickTurn) zRotation *= DriveConstants.kQuickTurnMultiplier
        differentialDrive.curvatureDrive(xSpeed, zRotation, isQuickTurn)

        if (isAligning && Limelight.hasTarget) {
            val kVi: Double
            if (xSpeed.epsilonEquals(0.0, 0.2)) {
                anglePID.kP = 0.6
                anglePID.kD = 0.05
                kVi = 0.2
            } else {
                left = 0.4
                right = 0.4
                anglePID.kP = 0.01
                anglePID.kD = 2.0 / (Limelight.area)
                kVi = 0.05
            }

            anglePID.dt = DriveMotionPlanner.dt
            val angleAdjustment = anglePID.updateByError(-Math.toRadians(Limelight.x))
            val friction = kVi.withSign(angleAdjustment)

            if (angleAdjustment >= 0) {
                Drive.leftDemand = left
                Drive.rightDemand = right + (angleAdjustment + friction)
            } else {
                Drive.leftDemand = left - (angleAdjustment + friction)
                Drive.rightDemand = right
            }
        } else {
            Drive.leftDemand = left
            Drive.rightDemand = right
        }
    }

    override val shouldFinish get() = false

    override fun stop() {
        Drive.controlMode = ControlMode.PercentOutput
        Drive.leftDemand = 0.0
        Drive.rightDemand = 0.0
    }
}