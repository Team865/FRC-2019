package ca.warp7.frc2019.test.drive

import ca.warp7.frc.control.config
import ca.warp7.frc.control.followedBy
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.math.withSign

class SimpleTrapezoidDistanceP : TimedRobot(){
    private val leftMaster: WPI_TalonSRX = WPI_TalonSRX(DriveConstants.kLeftMaster).apply {
        config(DriveConstants.kMasterTalonConfig)
        setNeutralMode(NeutralMode.Brake)
        enableVoltageCompensation(true)
        enableCurrentLimit(false)
        followedBy(VictorSPX(DriveConstants.kLeftFollowerA))
        followedBy(VictorSPX(DriveConstants.kLeftFollowerB))
        selectProfileSlot(0, 0)
        selectedSensorPosition = 0
    }

    private val rightMaster: WPI_TalonSRX = WPI_TalonSRX(DriveConstants.kRightMaster).apply {
        config(DriveConstants.kMasterTalonConfig)
        setNeutralMode(NeutralMode.Brake)
        enableVoltageCompensation(true)
        enableCurrentLimit(false)
        followedBy(VictorSPX(DriveConstants.kRightFollowerA))
        followedBy(VictorSPX(DriveConstants.kRightFollowerB))
        selectProfileSlot(0, 0)
        selectedSensorPosition = 0
    }

    private val tab = Shuffleboard.getTab("Drive Linear PID")

    val v: NetworkTableEntry = tab.add("Velocity", 0).entry

    var startTime = 0.0
    var timeSinceStart= 0.0
    var lastTime = 0.0
    var velocity= 0.0

    var startVelocity = 0.0 // feet per second squared
    var demandedDistance = 1.0 // feet
    var dxAtMaxTheoV = demandedDistance/ 2
    var maxTheoV = sqrt(pow(startVelocity, 2.0) + 2 * DriveConstants.kMaxAcceleration * dxAtMaxTheoV) // feet per second
    var isTriange = false
    var maxV = 0.0
    var dtAtMaxV = 0.0
    var dxAtMaxV= 0.0
    var dtInCruiseV= 0.0
    var totalDt= 0.0
    override fun autonomousInit() {
        startVelocity = 0.0 // feet per second squared
        demandedDistance = 12.0 // feet
        dxAtMaxTheoV = demandedDistance/ 2
        maxTheoV = sqrt(pow(startVelocity, 2.0) + 2 * DriveConstants.kMaxAcceleration * dxAtMaxTheoV) // feet per second
        println("max theov" + maxTheoV)
        isTriange = false
        maxV = 0.0
        dtAtMaxV = 0.0
        dxAtMaxV= 0.0
        dtInCruiseV= 0.0
        totalDt= 0.0

        if (maxTheoV < DriveConstants.kMaxVelocity) {
            println("tri")
            isTriange = true
            maxV = maxTheoV
            dtAtMaxV = (maxV - startVelocity)/DriveConstants.kMaxAcceleration
            dxAtMaxV = dxAtMaxTheoV
            dtInCruiseV = 0.0
        }
        else{
            isTriange = false
            maxV = DriveConstants.kMaxVelocity
            dtAtMaxV = (maxV - startVelocity)/DriveConstants.kMaxAcceleration
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
        println("startTime")
        println(startTime)
        position = 0.0
        leftMaster.selectedSensorPosition = 0
        rightMaster.selectedSensorPosition = 0
        trajectoryTime = 0.0
    }

    var position = 0.0
    var lastErrorLeft = 0.0
    var lastErrorRight = 0.0
    var lastErrorLeftVel = 0.0
    var lastErrorRightVel = 0.0
    var trajectoryTime = 0.0
    override fun autonomousPeriodic() {
        timeSinceStart = Timer.getFPGATimestamp() - startTime
        //println("time" + timeSinceStart)


        when {
            timeSinceStart < dtAtMaxV -> {
                velocity = timeSinceStart * DriveConstants.kMaxAcceleration
//                position = 0.5 * velocity * timeSinceStart
                println("velocity up" + velocity)
            }
            timeSinceStart < dtAtMaxV + dtInCruiseV -> {
                velocity = maxV
//                position = dxAtMaxV + (timeSinceStart - dtAtMaxV) * maxV
                println("velocity is" + velocity)
            }
            timeSinceStart < totalDt -> {
                velocity =  (totalDt - timeSinceStart) * DriveConstants.kMaxAcceleration
//                position = dxAtMaxV + dtInCruiseV * maxV +
                println("velocity down" + velocity)
            }
            else -> {
                velocity = 0.0
//                leftMaster.neutralOutput()
//                rightMaster.neutralOutput()
//                return
            }
        }
        position += velocity * (timeSinceStart - lastTime)

        val actualLeft = (leftMaster.selectedSensorPosition / DriveConstants.kTicksPerInch) / 12
        val actualRight = (-rightMaster.selectedSensorPosition / DriveConstants.kTicksPerInch) / 12

        val errorLeft = position - actualLeft
        val errorRight = position - actualRight

        val dErrorLeft = errorLeft - lastErrorLeft
        val dErrorRight = errorRight - lastErrorRight

        lastErrorLeft = errorLeft
        lastErrorRight = errorRight

        val kP = 0.05
        val kD = 0.2

        val leftP = errorLeft * kP
        val rightP = errorRight * kP

        val leftD = dErrorLeft * kD
        val rightD = dErrorRight * kD

        val actualLeftVelocity = (leftMaster.selectedSensorVelocity / DriveConstants.kTicksPerInch * 10) / 12
        val actualRightVelocity = (-rightMaster.selectedSensorVelocity / DriveConstants.kTicksPerInch * 10) / 12

        val kD2 = 0.8

        val errorLeftVel = velocity - actualLeftVelocity
        val errorRightVel = velocity - actualRightVelocity

        val dErrorLeftVal = errorLeftVel - lastErrorLeftVel
        val dErrorRightVel = errorRightVel - lastErrorRightVel

        lastErrorLeftVel = errorLeftVel
        lastErrorRightVel = errorRightVel

        val leftDVel = dErrorLeftVal * kD2
        val rightDVel = dErrorRightVel * kD2

        println("Position:$position\t Left:$actualLeft\t Right:$actualRight\t LeftDVel: $leftDVel\t RightDVel: $rightDVel")

        v.setDouble(velocity)
        leftMaster.set(min(abs(velocity) / DriveConstants.kMaxVelocity, 1.0).withSign(velocity) + leftP + leftD + leftDVel)
        rightMaster.set(-1.0 *(min(abs(velocity) / DriveConstants.kMaxVelocity, 1.0).withSign(velocity)) - rightP - rightD - rightDVel)
        lastTime = timeSinceStart
    }
}