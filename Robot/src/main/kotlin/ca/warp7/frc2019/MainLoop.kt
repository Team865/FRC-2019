package ca.warp7.frc2019

import ca.warp7.actionkt.*
import ca.warp7.frc.ControllerState.HeldDown
import ca.warp7.frc.ControllerState.Pressed
import ca.warp7.frc.set
import ca.warp7.frc.withDriver
import ca.warp7.frc.withOperator
import ca.warp7.frc2019.constants.ControlConstants
import ca.warp7.frc2019.constants.HatchCargo
import ca.warp7.frc2019.constants.SuperstructureConstants
import ca.warp7.frc2019.subsystems.*
import ca.warp7.frc2019.subsystems.drive.DriveState
import ca.warp7.frc2019.subsystems.lift.LiftState
import ca.warp7.frc2019.subsystems.superstructure.SuperstructureState
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.Watchdog
import java.util.*

object MainLoop : Action {

    override fun start() {
        println("Robot State: Teleop")
        Drive.set(DriveState.kNeutralOutput)
    }

    override val shouldFinish: Boolean = false

    override fun update() {
        var passThroughSpeed = 0.0
        var isOuttaking = false
        withDriver {
            Drive.set(DriveState.kCurvature) {
                xSpeed = leftYAxis * -1
                zRotation = rightXAxis
                isQuickTurn = leftBumper == HeldDown
            }
            when {
                leftTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = -1 * leftTriggerAxis
                    isOuttaking = true
                    Intake.set {
                        speed = -1 * leftTriggerAxis * SuperstructureConstants.kIntakeSpeedScale
                        extended = true
                    }
                }
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = rightTriggerAxis
                    isOuttaking = rightBumper == HeldDown
                    Intake.set {
                        speed = rightTriggerAxis * SuperstructureConstants.kIntakeSpeedScale
                        extended = true
                    }
                }
                else -> {
                    Intake.set {
                        speed = 0.0
                        extended = false
                    }
                }
            }
        }
        withOperator {
            when {
                leftTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = -1 * leftTriggerAxis
                    isOuttaking = true
                }
                rightTriggerAxis > ControlConstants.kControlDeadband -> {
                    passThroughSpeed = rightTriggerAxis
                    isOuttaking = true
                }
            }
            Lift.set(LiftState.kOpenLoop) { speed = leftYAxis }
            when (Pressed) {
                leftBumper -> if (Lift.setpointLevel < 3) {
                    Lift.setpointLevel++
                    Lift.set(LiftState.kFollowTrajectory) { setpoint = Lift.coolSetpoint }
                }
                rightBumper -> if (Lift.setpointLevel > 0) {
                    Lift.setpointLevel--
                    Lift.set(LiftState.kFollowTrajectory) { setpoint = Lift.coolSetpoint }
                }
/*
                aButton -> {
                    Lift.setpointType = HatchCargo.Hatch
                    Lift.set(LiftState.kFollowTrajectory) { setpoint = Lift.coolSetpoint }
                }
                bButton -> {
                    Lift.setpointType = HatchCargo.Cargo
                    Lift.set(LiftState.kFollowTrajectory) { setpoint = Lift.coolSetpoint }
                }*/

/*
                xButton -> if (Outtake.grabbing) {
                    Outtake.grabbing = false
                    Outtake.pushing = true
                    Outtake.set(runAfter(0.2) {
                        Outtake.pushing = false
                    })
                } else {
                    Outtake.grabbing = true
                    Outtake.pushing = false
                }
                */
//                    Outtake.set {
//                        if (grabbing) {
//                            grabbing = false
//                            set(queue {
//                                println("creating queue")
//                                +runOnce {
//                                    println("grabbing: false")
//                                    grabbing = false
//                                }
//                                +wait(0.05)
//                                +runOnce {
//                                    println("pushing:true")
//                                    pushing = true
//                                }
//                                +wait(0.3)
//                                +runOnce {
//                                    println("pushing: false")
//                                    pushing = false
//                                }
//                            })
//                        } else {
//                            grabbing = true
//                            pushing = false
//                        }


                    bButton -> Outtake.set {
                        grabbing = !grabbing
                        pushing = false
                    }
                    aButton -> Outtake.set {
                        pushing = !pushing
                        grabbing = false
                    }


                else -> Unit


            }
        }
        if (passThroughSpeed != 0.0) {
            Superstructure.set(SuperstructureState.kPassThrough) {
                speed = passThroughSpeed
                outtaking = isOuttaking
            }
        } else {
            Superstructure.set(SuperstructureState.kIdle)
        }
    }
}