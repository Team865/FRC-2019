package ca.warp7.frc2019.test.drive

import ca.warp7.frc.config
import ca.warp7.frc.followedBy
import ca.warp7.frc2019.constants.DriveConstants
import ca.warp7.frc2019.subsystems.Drive
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.Timer
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.math.withSign

abstract class simpleTrapezoidDistance : TimedRobot(){
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
    var startTime = 0.0
    var timeSinceStart = 0.0
    var lastTime = 0.0
    var velocity = 0.0
    val time=0.7
    val startVelocity = 0.0 // feet per second squared
    val demandedDistance = 3.0 // feet
    val dxAtMaxTheoV = demandedDistance/ 2
    val maxTheoV = sqrt(pow(startVelocity, 2.0) + 2 * DriveConstants.kMaxAcceleration * dxAtMaxTheoV) // feet per second
    abstract var isTriangle : Boolean
    abstract var maxV : Double
    abstract var dtAtMaxV : Double
    abstract var dxAtMaxV: Double
    abstract var dtInCruiseV: Double
    abstract var totalDt: Double
    override fun autonomousInit() {
        if (maxTheoV >= DriveConstants.kMaxVelocity) {
            isTriangle = true
            maxV = maxTheoV
            dtAtMaxV = (maxV - startVelocity)/DriveConstants.kMaxAcceleration
            dxAtMaxV = dxAtMaxTheoV
            dtInCruiseV = 0.0
        }
        else{
            isTriangle = false
            maxV = DriveConstants.kMaxVelocity
            dtAtMaxV = (maxV - startVelocity)/DriveConstants.kMaxAcceleration
            dxAtMaxV = (startVelocity + DriveConstants.kMaxVelocity) / 2 * dtAtMaxV
            dtInCruiseV = demandedDistance - (2 * dxAtMaxV)
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
        val dt = timeSinceStart - lastTime



        println(timeSinceStart)

        when {
            timeSinceStart < dtAtMaxV -> velocity = timeSinceStart * DriveConstants.kMaxAcceleration * DriveConstants.kTicksPerInch
            timeSinceStart < dtAtMaxV + dtInCruiseV -> velocity = maxV * DriveConstants.kTicksPerInch
            timeSinceStart < totalDt -> velocity =  (totalDt - timeSinceStart) * DriveConstants.kMaxAcceleration * DriveConstants.kTicksPerInch
            else -> velocity = 0.0
        }

        println(velocity)
        leftMaster.set(min(abs(velocity), DriveConstants.kMaxVelocity * DriveConstants.kTicksPerInch).withSign(velocity))
        rightMaster.set(-1.0 * min(abs(velocity), DriveConstants.kMaxVelocity * DriveConstants.kTicksPerInch).withSign(velocity))



        lastTime = timeSinceStart
    }
}