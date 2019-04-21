package ca.warp7.frc2019.subsystems.lift.deprecated

import ca.warp7.actionkt.Action
import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.v2.subsystems.Lift
import com.ctre.phoenix.motorcontrol.ControlMode
import java.lang.Math.signum
import kotlin.math.abs
import kotlin.math.pow

object GoToPositionMotionPlanningSimple : Action {
    private val io: RobotIO = RobotIO
    var heightInputAbsoluteInches = 0.0
    var targetHeightFromHome = 0.0

    override fun start() {
        io.liftControlMode = ControlMode.Velocity
    }

    override fun update() {
        targetHeightFromHome = heightInputAbsoluteInches - LiftConstants.kHomeHeightInches
        val relativeDistanceToTarget = targetHeightFromHome - Lift.height
        if (shouldDecelerate(io.liftVelocity.toDouble(), relativeDistanceToTarget)) {
            io.liftDemand = LiftConstants.kMaxVelocityInchesPerSecond * signum(relativeDistanceToTarget)
        } else {
            io.liftDemand = 0.0
        }
    }

    override val shouldFinish: Boolean
        get() {
            return Lift.height == targetHeightFromHome && io.liftVelocity == io.liftDemand.toInt()
        }

    fun shouldDecelerate(relativeHeight: Double, currentVelocity: Double): Boolean {
        val startDeceletatingAtHeightRelativeToTarget = -1 * currentVelocity.pow(2) / (2 * LiftConstants.kMaxBaseAcceleration)
        return (startDeceletatingAtHeightRelativeToTarget >= abs(relativeHeight))
    }
}