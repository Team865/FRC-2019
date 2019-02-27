package ca.warp7.frc2019.test.drive

import ca.warp7.frc.config
import ca.warp7.frc.followedBy
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

class SimpleTrapezoidDistance : TimedRobot(){
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
    }

    override fun autonomousPeriodic() {

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
                velocity =  (totalDt - timeSinceStart) * DriveConstants.kMaxAcceleration
                println("velocity down" + velocity)
            }
            else -> velocity = 0.0
        }

        v.setDouble(velocity)
        leftMaster.set(min(abs(velocity) / DriveConstants.kMaxVelocity, 1.0).withSign(velocity) + 0.02.withSign(velocity))
        rightMaster.set(-1.0 *(min(abs(velocity) / DriveConstants.kMaxVelocity, 1.0).withSign(velocity) + 0.02.withSign(velocity)))
        lastTime = timeSinceStart
    }
}