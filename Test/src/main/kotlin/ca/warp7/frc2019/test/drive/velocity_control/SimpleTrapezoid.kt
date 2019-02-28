package ca.warp7.frc2019.test.drive.velocity_control

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.math.withSign

object SimpleTrapezoid : Action {
    var startTime = 0.0
    var timeSinceStart = 0.0
    var lastTime = 0.0
    var velocity = 0.0

    var startVelocity = 0.0 // feet per second squared
    var demandedDistance = 12.0 // feet
    var dxAtMaxTheoV = demandedDistance / 2
    var maxTheoV = sqrt(Math.pow(startVelocity, 2.0) +
            2 * DriveConstants.kMaxAcceleration * dxAtMaxTheoV) // feet per second
    var isTriange = false
    var maxV = 0.0
    var dtAtMaxV = 0.0
    var dxAtMaxV = 0.0
    var dtInCruiseV = 0.0
    var totalDt = 0.0

    var position = 0.0

    override fun start() {

        if (maxTheoV < DriveConstants.kMaxVelocity) {
            println("tri")
            isTriange = true
            maxV = maxTheoV
            dtAtMaxV = (maxV - startVelocity) / DriveConstants.kMaxAcceleration
            dxAtMaxV = dxAtMaxTheoV
            dtInCruiseV = 0.0
        } else {
            isTriange = false
            maxV = DriveConstants.kMaxVelocity
            dtAtMaxV = (maxV - startVelocity) / DriveConstants.kMaxAcceleration
            println("dtatmaxV" + dtAtMaxV)
            dxAtMaxV = (startVelocity + DriveConstants.kMaxVelocity) / 2 * dtAtMaxV
            println("dxatmaxV" + dxAtMaxV)
            dtInCruiseV = demandedDistance - (2 * dxAtMaxV)
            println("dtInCruiseV" + dtInCruiseV)
        }
        totalDt = dtInCruiseV + dtAtMaxV * 2
        timeSinceStart = 0.0
        lastTime = 0.0
        velocity = 0.0

        startTime = Timer.getFPGATimestamp()
        println("startTime: $startTime")
        position = 0.0
    }

    override fun update() {
        timeSinceStart = Timer.getFPGATimestamp() - startTime
        //println("time" + timeSinceStart)
        when {
            timeSinceStart < dtAtMaxV -> {
                velocity = timeSinceStart * DriveConstants.kMaxAcceleration
                println("velocity up" + velocity)
            }
            timeSinceStart < dtAtMaxV + dtInCruiseV -> {
                velocity = maxV
                println("velocity is" + velocity)
            }
            timeSinceStart < totalDt -> {
                velocity = (totalDt - timeSinceStart) * DriveConstants.kMaxAcceleration
                println("velocity down" + velocity)
            }
            else -> {
                velocity = 0.0
            }
        }

        Drive.controlMode = ControlMode.PercentOutput
        Drive.leftDemand = min(abs(velocity) / DriveConstants.kMaxVelocity, 1.0).withSign(velocity)
        Drive.rightDemand = min(abs(velocity) / DriveConstants.kMaxVelocity, 1.0).withSign(velocity)
        lastTime = timeSinceStart
    }
}