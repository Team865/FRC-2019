package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.speedController
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.drive.DifferentialDrive

object CurvatureDrive : Action {
    var xSpeed = 0.0
    var zRotation = 0.0
    var isQuickTurn = false


    var left = 0.0
    var right = 0.0

    private val differentialDrive = DifferentialDrive(
            //linearRamp(DriveConstants.kRampSecondsFromNeutralToFull) { left = it },
            //linearRamp(DriveConstants.kRampSecondsFromNeutralToFull) { right = it }
            speedController { left = it },
            speedController { right = it }
    )

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
        if (xSpeed < -ControlConstants.kControlDeadband || isQuickTurn) zRotation *= -1
        differentialDrive.curvatureDrive(xSpeed, zRotation, isQuickTurn)
        Drive.leftDemand = left
        Drive.rightDemand = right
    }

    override val shouldFinish get() = false

    override fun stop() {
        Drive.set(DriveState.kNeutralOutput)
    }
}