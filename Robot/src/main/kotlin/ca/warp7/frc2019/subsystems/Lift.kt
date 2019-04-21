package ca.warp7.frc2019.subsystems

import ca.warp7.frc2019.RobotIO
import ca.warp7.frc2019.constants.FieldConstants
import ca.warp7.frc2019.constants.HatchCargo
import ca.warp7.frc2019.constants.LiftConstants
import com.ctre.phoenix.motorcontrol.ControlMode
import kotlin.math.withSign

object Lift {

    private val io: RobotIO = RobotIO

    var setpointLevel = 0
    var setpointType = HatchCargo.Hatch

    fun getCoolSetpoint(): Double = when (setpointLevel) {
        0 -> when (setpointType) {
            HatchCargo.Hatch -> LiftConstants.kHomeHeightInches + 3.0
            HatchCargo.Cargo -> FieldConstants.kCargo1Height
        }
        1 -> when (setpointType) {
            HatchCargo.Hatch -> FieldConstants.kHatch2Height + 3.0
            HatchCargo.Cargo -> FieldConstants.kCargo2Height
        }
        2 -> when (setpointType) {
            HatchCargo.Hatch -> FieldConstants.kHatch3Height + 3.0
            HatchCargo.Cargo -> FieldConstants.kCargo3Height
        }
        else -> LiftConstants.kHomeHeightInches
    }

    fun increaseSetpoint() {
        setpointLevel = (setpointLevel + 1).coerceAtMost(2)
    }

    fun decreaseSetpoint() {
        setpointLevel = (setpointLevel - 1).coerceAtLeast(0)
    }

    var nominalZero = 0
    var feedforwardEnabled = true
    var isManual = false
    var manualSpeed = 0.0
    var setpointInches = 0.0

    val adjustedPositionTicks get() = io.liftPosition - nominalZero
    val height get() = adjustedPositionTicks / LiftConstants.kTicksPerInch

    fun setFeedforward() {
        io.liftFeedforward = if (feedforwardEnabled) LiftConstants.kPrimaryFeedforward else 0.0
    }

    fun updateManualControl() {
        io.liftControlMode = ControlMode.PercentOutput
        io.liftDemand = (manualSpeed * manualSpeed).withSign(manualSpeed)
        setFeedforward()
    }

    fun updatePositionControl() {
        if (io.hallEffectTriggered) {
            nominalZero = io.liftPosition
        }
        if (setpointInches < LiftConstants.kPIDDeadSpotHeight
                || height < LiftConstants.kPIDDeadSpotHeight
                || io.hallEffectTriggered) {
            io.liftControlMode = ControlMode.Position
            io.liftDemand = -(setpointInches * LiftConstants.kTicksPerInch) + nominalZero
            setFeedforward()
        } else {
            io.liftControlMode = ControlMode.PercentOutput
            io.liftDemand = LiftConstants.kMoveToBottomDemand
            io.liftFeedforward = 0.0
        }
    }
}