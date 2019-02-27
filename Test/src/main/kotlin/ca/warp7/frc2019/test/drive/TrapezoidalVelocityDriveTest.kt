package ca.warp7.frc2019.test.drive

import ca.warp7.frc.config
import ca.warp7.frc.followedBy
import ca.warp7.frc2019.constants.DriveConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.Timer
import kotlin.math.pow
import kotlin.math.sqrt


class TrapezoidalVelocityDriveTest: TimedRobot() {
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
    private var startTime = 0.0
    private var currentTargetVelocity = 0.0
    private var timeSinceStart = 0.0
    private var startAndEndVelocity = 0.0
    private var targetLinearChange = 0.0
    private var maxAccel = 0.0
    private var isTriange = false
    private var totalTimeEstimate = 0.0
    private var timeUntilMaxVelocityReachedEstimate = 0.0
    private var maxReachedVelocity = 0.0


    private var running = false

    fun start() {
        running = true
        currentTargetVelocity = (leftMaster.selectedSensorVelocity * 1.0 + rightMaster.selectedSensorVelocity * 1.0) / 2
        timeSinceStart = 0.0

    }

    fun setTarget(targetx: Double, targetStartAndEndVelocity: Double, maxAcceleration: Double = 8.5){
        targetLinearChange = targetx
        println("targetx")
        println(targetLinearChange)
        startAndEndVelocity = targetStartAndEndVelocity
        maxAccel = maxAcceleration
        var maxPhysicalPotentialVelocity = 5.0
        generateTrajectory(maxPhysicalPotentialVelocity)
    }
    fun generateTrajectory(maxPhysicalVelocity: Double){
        var linearChangeAtMaxTheoreticalVelocity = targetLinearChange / 2
        var maximumTheorecticallyReachableVelocity = sqrt((startAndEndVelocity.pow(2)) + 2 * maxAccel * linearChangeAtMaxTheoreticalVelocity)
        if (maxPhysicalVelocity >= maximumTheorecticallyReachableVelocity){
            isTriange = true
            maxReachedVelocity = maximumTheorecticallyReachableVelocity
            println("maxVel")
            println(maxReachedVelocity)
        }
        else {
            isTriange = false
            maxReachedVelocity = maxPhysicalVelocity
        }
        println("maxVel")
        println(maxReachedVelocity)
        println("triangle")
        println(isTriange)

        timeUntilMaxVelocityReachedEstimate = (maxReachedVelocity - startAndEndVelocity) / maxAccel

        if (isTriange){
            totalTimeEstimate = 2 * timeUntilMaxVelocityReachedEstimate
        }
        else{
            var dxtomaxV = (startAndEndVelocity + maxReachedVelocity) / 2 * timeUntilMaxVelocityReachedEstimate
            var dxatcruiseV = targetLinearChange - dxtomaxV * 2
            var dtatcruiseV = dxatcruiseV / maxReachedVelocity
            println("dt to max v")
            println(dtatcruiseV)
            var tAcandDc = 2 * timeUntilMaxVelocityReachedEstimate
            totalTimeEstimate = dtatcruiseV + tAcandDc
        }
    }

    fun update(timeSinceStart: Double){

        if (timeSinceStart > totalTimeEstimate){
            currentTargetVelocity = 0.0
        }

        if (isTriange){
            if (timeSinceStart <= timeUntilMaxVelocityReachedEstimate){
                currentTargetVelocity = startAndEndVelocity + timeSinceStart * maxAccel
            }
            else{
                currentTargetVelocity = maxReachedVelocity - (timeSinceStart - timeUntilMaxVelocityReachedEstimate) * maxAccel
            }
        }
        else{
            if (timeSinceStart <= timeUntilMaxVelocityReachedEstimate){
                currentTargetVelocity = startAndEndVelocity + timeSinceStart * maxAccel
            }
            else if (timeSinceStart >= totalTimeEstimate - timeUntilMaxVelocityReachedEstimate){
                currentTargetVelocity = startAndEndVelocity + (totalTimeEstimate - timeSinceStart) * maxAccel
            }
            else{
                currentTargetVelocity = maxReachedVelocity
            }
        }

    }

    override fun robotPeriodic() {
        update(startTime - Timer.getFPGATimestamp())
    }

    override fun robotInit() {
        println("Hello me is robit!")
    }

    override fun autonomousInit() {
        println("Hello me is autp robit!")
        setTarget(1000.0, 0.0)
        start()
    }
    override fun autonomousPeriodic() {
        startTime = Timer.getFPGATimestamp()
        println("vel")
        println(currentTargetVelocity)
        leftMaster.set(ControlMode.Velocity, currentTargetVelocity)
        rightMaster.set(ControlMode.Velocity, currentTargetVelocity)
    }

    override fun disabledInit() {
        leftMaster.disable()
        rightMaster.disable()
    }
}