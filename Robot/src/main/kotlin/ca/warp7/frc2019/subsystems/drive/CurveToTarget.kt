package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.speedController
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Limelight
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import kotlin.math.absoluteValue

object CurveToTarget : Action {
    var xSpeed = 0.0
    var zRotation = 0.0
    var isQuickTurn = false

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
        Drive.leftDemand = left
        Drive.rightDemand = right
        if (Limelight.hasTarget) {
            val error = Math.toRadians(Limelight.x)  // MAX +-0.5
            val kP = 1
            var leftAdjustment = error * kP
            var rightAdjustment = -error * kP
            val speed = xSpeed.absoluteValue
            if (speed > ControlConstants.kControlDeadband){
                leftAdjustment /= speed
                rightAdjustment /= speed
            } else{
                leftAdjustment*=2
                rightAdjustment*=2
            }
            Drive.leftFeedforward = leftAdjustment
            Drive.rightFeedforward = rightAdjustment
        } else {
            Drive.leftFeedforward = 0.0
            Drive.rightFeedforward = 0.0
        }
    }

    override val shouldFinish get() = false

    override fun stop() {
        Drive.controlMode = ControlMode.PercentOutput
        Drive.leftDemand = 0.0
        Drive.rightDemand = 0.0
    }
}