package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.*
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.sign
import kotlin.math.withSign

class QuickTurn(angleInDegrees: Double) : Action {

    private var targetYaw = Rotation2D.fromDegrees(angleInDegrees)
    private var startYaw = Rotation2D.identity
    private var error = 0.0
    private var dError = 0.0
    private var sumError = 0.0

    override fun start() {
        Drive.controlMode = ControlMode.PercentOutput
        startYaw = Infrastructure.yaw
        targetYaw += startYaw
        error = startYaw.radians
    }

    private val angularKp = 0.75
    private val angularKd = 0.15
    private val angularKi = 0.001
    private val integralZone = 0.75

    override fun update() {
        val newError = (targetYaw - Infrastructure.yaw).radians
        dError = (newError - error) / DriveMotionPlanner.lastDt

        if (error.sign != newError.sign) sumError = 0.0
        else if (!error.epsilonEquals(0.0, integralZone)) sumError += integralZone.withSign(sumError)
        else sumError += error

        val angularGain = error * angularKp + dError * angularKd + sumError * angularKi
        Drive.leftDemand = angularGain
        Drive.rightDemand = -angularGain
        error = newError
        Drive.put("Qt Error", error)
        Drive.put("Qt dError", dError)
        Drive.put("Qt SumError", sumError)
    }

    override val shouldFinish: Boolean
        get() = error < 0.1 && dError < 0.1

    override fun stop() {
        Drive.apply {
            leftDemand = 0.0
            rightDemand = 0.0
            leftFeedforward = 0.0
            rightFeedforward = 0.0
        }
    }
}