package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.speedController
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Limelight
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import kotlin.math.sign
import kotlin.math.withSign

object CurveToTarget : Action {
    var xSpeed = 0.0
    var zRotation = 0.0
    var isQuickTurn = false
    var isAligning = false

    var left = 0.0
    var right = 0.0

    var pHasTarget = false
    var pError = 0.0
    var pTime = 0.0
    private val tab = Shuffleboard.getTab("Curve To Target")
    val p: NetworkTableEntry = tab.add("P", 0).withWidget(BuiltInWidgets.kNumberSlider).entry
    val D: NetworkTableEntry = tab.add("D", 0).withWidget(BuiltInWidgets.kNumberSlider).entry

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
            val dt = time - pTime
            if (error.epsilonEquals(0.0, 0.02)) {
                Drive.leftDemand = left
                Drive.rightDemand = right
            } else {
                val dError = error - pError

                var kP: Double
                var kD: Double
                var kVi: Double

                if (xSpeed.epsilonEquals(0.0, 0.1)) {
                    kP = 0.01
                    kD = 2.0
                    kVi = 0.2
                } else {
                    kP = 0.5
                    kD = 0.05
                    kVi = 0.2
                }

                val frictionVoltage = kVi.withSign(error)
                Drive.leftDemand = left*0.5 + ((error * kP + dError / dt * kD + frictionVoltage) / (Limelight.area))
                Drive.rightDemand = right*0.5 - ((error * kP + dError / dt * kD + frictionVoltage) / (Limelight.area))
            }
            pError = error
            pTime = time
        } else {
            Drive.leftDemand = left
            Drive.rightDemand = right
        }
        pHasTarget = Limelight.hasTarget
    }

    override val shouldFinish get() = false

    override fun stop() {
        Drive.controlMode = ControlMode.PercentOutput
        Drive.leftDemand = 0.0
        Drive.rightDemand = 0.0
    }
}