package ca.warp7.frc2019.subsystems.drive.unused

import ca.warp7.actionkt.Action
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.radians
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.drive.DriveMotionPlanner
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.absoluteValue
import kotlin.math.withSign

class PIDToPoint(private var targetState: Pose2D) : Action {

    override fun start() {
        targetState = DriveMotionPlanner.robotState + targetState
        Drive.controlMode = ControlMode.Position
    }

    val forwardGains = PID(kP = 1.0, kI = 0.0, kD = 0.0)
    val lateralGains = PID(kP = 1.0, kI = 0.0, kD = 0.0)
    val angularGains = PID(kP = 1.0, kI = 0.0, kD = 0.0)

    override fun update() {
        val actual = DriveMotionPlanner.robotState
        val dt = DriveMotionPlanner.dt
        forwardGains.setpoint = targetState.translation.x
        lateralGains.setpoint = targetState.translation.y
        angularGains.setpoint = targetState.rotation.radians
        forwardGains.dt = dt
        lateralGains.dt = dt
        angularGains.dt = dt
        val forward = forwardGains.updateBySetpoint(actual.translation.x)
        val lateral = lateralGains.updateBySetpoint(actual.translation.y).withSign(forward)
        val angular = angularGains.updateBySetpoint(actual.rotation.radians)
        var leftAngular = -angular
        var rightAngular = angular
        if (angular.absoluteValue > lateral.absoluteValue) {
            if (forward > 0) {
                if (angular > 0) leftAngular = 0.0
                else rightAngular = 0.0
            } else {
                if (angular > 0) rightAngular = 0.0
                else leftAngular = 0.0
            }
        }
        Drive.leftDemand = (forward - lateral + leftAngular).coerceIn(-1.0, 1.0)
        Drive.rightDemand = (forward + lateral + rightAngular).coerceIn(-1.0, 1.0)
    }
}