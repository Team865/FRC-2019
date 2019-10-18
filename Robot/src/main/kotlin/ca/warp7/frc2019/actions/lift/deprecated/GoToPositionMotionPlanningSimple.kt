package ca.warp7.frc2019.actions.lift.deprecated

import ca.warp7.frc.action.Action
import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Lift
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

object GoToPositionMotionPlanningSimple : Action {
    private val io: BaseIO = ioInstance()
    var heightInputAbsoluteInches = 0.0
    var targetHeightFromHome = 0.0

    override fun firstCycle() {
        io.liftControlMode = ControlMode.Velocity
    }

    override fun update() {
        targetHeightFromHome = heightInputAbsoluteInches - LiftConstants.kHomeHeightInches
        val relativeDistanceToTarget = targetHeightFromHome - Lift.height
        if (shouldDecelerate(io.liftVelocity.toDouble(), relativeDistanceToTarget)) {
            io.liftDemand = LiftConstants.kMaxVelocityInchesPerSecond * sign(relativeDistanceToTarget)
        } else {
            io.liftDemand = 0.0
        }
    }

    override fun shouldFinish(): Boolean {
        return Lift.height == targetHeightFromHome && io.liftVelocity == io.liftDemand.toInt()
    }

    fun shouldDecelerate(relativeHeight: Double, currentVelocity: Double): Boolean {
        val startDeceletatingAtHeightRelativeToTarget = -1 * currentVelocity.pow(2) / (2 * LiftConstants.kMaxBaseAcceleration)
        return (startDeceletatingAtHeightRelativeToTarget >= abs(relativeHeight))
    }
}