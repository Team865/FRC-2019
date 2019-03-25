package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.speedController
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Limelight
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import kotlin.math.withSign

class AlignedCurvature : Action {
    var xSpeed = 0.0
    var zRotation = 0.0
    var isQuickTurn = false
    var isAligning = false

    var left = 0.0
    var right = 0.0

    private var lastError = 0.0
    private var lastTime = 0.0

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
            val time = Timer.getFPGATimestamp()
            val dt = time - lastTime
            if (error.epsilonEquals(0.0, 0.02)) {
                Drive.leftDemand = left
                Drive.rightDemand = right
            } else {
                val dError = error - lastError
                val kP: Double
                val kD: Double
                val kVi: Double
                if (!xSpeed.epsilonEquals(0.0, 0.2)) {
                    left=0.4
                    right=0.4
                    kP = 0.01
                    kD = 2.0 / (Limelight.area)
                    kVi = 0.2
                } else {
                    kP = 0.6
                    kD = 0.05
                    kVi = 0.2
                }
                val friction = kVi.withSign(error)
                Drive.leftDemand = left + (error * kP + dError / dt * kD + friction)
                Drive.rightDemand = right  - (error * kP + dError / dt * kD + friction)
                println((Drive.leftVelocity + Drive.rightVelocity) / 2.0)
            }
            lastError = error
            lastTime = time
        } else {
            Drive.leftDemand = left
            Drive.rightDemand = right * 0.95
        }
    }

    override val shouldFinish get() = false

    override fun stop() {
        Drive.controlMode = ControlMode.PercentOutput
        Drive.leftDemand = 0.0
        Drive.rightDemand = 0.0
    }
}