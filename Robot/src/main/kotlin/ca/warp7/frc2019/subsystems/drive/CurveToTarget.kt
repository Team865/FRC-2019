package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.speedController
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Limelight
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import kotlin.math.withSign

object CurveToTarget : Action {
    var xSpeed = 0.0
    var zRotation = 0.0
    var isQuickTurn = false
    var isAligning = false

    var left = 0.0
    var right = 0.0

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
            val error = Math.toRadians(Limelight.x)
            if (error.epsilonEquals(0.0, 0.02)) {
                Drive.leftDemand = left
                Drive.rightDemand = right
            } else {
                val kP = 0.5
                val kVi = 0.2
                val frictionVoltage = kVi.withSign(error)
                Drive.leftDemand = left + error * kP + frictionVoltage
                Drive.rightDemand = right - error * kP - frictionVoltage
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