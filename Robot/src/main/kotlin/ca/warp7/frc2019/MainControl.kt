package ca.warp7.frc2019

import ca.warp7.frc.ControlLoop
import ca.warp7.frc.ControllerState.HeldDown
import ca.warp7.frc.ControllerState.Pressed
import ca.warp7.frc.Controls
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.states.DriveState
import ca.warp7.frc2019.states.SuperstructureState
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Superstructure

object MainControl : ControlLoop {

    override fun setup() {
        println("Robot State: Teleop")
    }

    override fun periodic() {

        var overrideDefence = false

        with(Controls.driver) {

            Drive.set(DriveState.Curvature) {
                xSpeed = leftYAxis
                zRotation = rightXAxis
                isQuickTurn = leftBumper == HeldDown
            }

            if (leftTriggerAxis > ControlConstants.kCargoPassDeadband) {
                Superstructure.set(SuperstructureState.PassingCargo) {
                    speed = leftTriggerAxis
                    deadband = ControlConstants.kCargoPassDeadband
                }
            } else if (rightTriggerAxis > ControlConstants.kCargoPassDeadband) {
                Superstructure.set(SuperstructureState.FeedingCargo) {
                    speed = leftTriggerAxis
                    deadband = ControlConstants.kCargoPassDeadband
                }
            }

            if (rightBumper == Pressed) overrideDefence = true
            if (backButton == Pressed) Superstructure.set(SuperstructureState.Idle)
            if (startButton == Pressed) Superstructure.set(SuperstructureState.MovingToClimb)
        }

        with(Controls.operator) {
            leftTriggerAxis // TODO Intake, overriding the driver
            rightTriggerAxis // TODO Outtake, overriding the driver

            when (Pressed) {
                leftBumper -> TODO("decrease set point on lift")
                rightBumper -> TODO("increase set point on lift")
                aButton -> TODO("Raise lift to the specified set point for the cargo")
                bButton -> TODO("Raise lift to the specified setpoint for the hatch panel")
                xButton -> TODO("Toggle the secondary intake to hold or release grip on the hatch panel")
                yButton -> TODO("Outtake the hatch panel by releasing the grip and push out with piston")
                else -> {
                }
            }

            if (leftStickButton == HeldDown) {

            }
        }

        if (overrideDefence) {
            Superstructure.set(SuperstructureState.MovingToDefence)
        }
    }
}