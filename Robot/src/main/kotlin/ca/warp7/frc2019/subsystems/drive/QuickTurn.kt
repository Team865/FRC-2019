package ca.warp7.frc2019.subsystems.drive

import ca.warp7.actionkt.Action
import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.degrees
import ca.warp7.frc.geometry.fromDegrees
import ca.warp7.frc.geometry.radians
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Infrastructure
import ca.warp7.frc2019.subsystems.Outtake
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.sign
import kotlin.math.withSign

class QuickTurn(angleInDegrees: Double) : Action {

    private var targetYaw = Rotation2D.fromDegrees(angleInDegrees)
    private var startYaw = Rotation2D.identity
    private var error = 0.0
    private var dError = 0.0
    private var sumError = 0.0
    private var t = 0.0

    override fun start() {
        Drive.controlMode = ControlMode.PercentOutput
        startYaw = Infrastructure.yaw
        error = targetYaw.radians
        targetYaw += startYaw
    }

    private val angularKp = 0.03
    private val angularKd = 0.0075
    private val angularKi = 0.00001//2
    private val integralZone = 10.0

    private val feedForwards = 0.0//3
    private val feedTime = 0.0//3

    override fun update() {
        t += DriveMotionPlanner.lastDt

        val newError = (targetYaw - Infrastructure.yaw).degrees
        dError = (newError - error) / DriveMotionPlanner.lastDt

        if (error.sign != newError.sign) sumError = 0.0
        else if (!error.epsilonEquals(0.0, integralZone)) sumError += integralZone.withSign(newError)
        else sumError += newError

        val feed: Double
        if (t < feedTime) feed = feedForwards.withSign(newError) / t
        else feed = 0.0

        val angularGain = error * angularKp + dError * angularKd + sumError * angularKi
        Drive.leftDemand = angularGain + feed
        Drive.rightDemand = -angularGain - feed
        error = newError
//        Drive.put("Qt Error", error)
//        Drive.put("Qt dError", dError)
//        Drive.put("Qt SumError", sumError)
//        println("ERROR $error")
    }

    override val shouldFinish
        get() = error.epsilonEquals(0.0, 4.0)
                && dError.epsilonEquals(0.0, 0.1)


    override fun stop() {
        Drive.apply {
            leftDemand = 0.0
            rightDemand = 0.0
            leftFeedforward = 0.0
            rightFeedforward = 0.0
        }
        Outtake.pushing=true
        Outtake.grabbing=false
    }
}
