package ca.warp7.frc2019

import ca.warp7.frc.action.*
import ca.warp7.frc.applyDeadband
//import ca.warp7.frc.control.LatchedBoolean
import ca.warp7.frc.input.ButtonState.*
//import ca.warp7.frc2019.constants.FieldConstants
//import ca.warp7.frc2019.constants.HatchCargo
//import ca.warp7.frc2019.constants.LiftConstants
import ca.warp7.frc2019.constants.LimelightMode
import ca.warp7.frc2019.io.BaseIO
import ca.warp7.frc2019.io.ioInstance
import ca.warp7.frc2019.subsystems.Drive
import ca.warp7.frc2019.subsystems.Lift
import ca.warp7.frc2019.subsystems.Limelight
import com.ctre.phoenix.motorcontrol.ControlMode

class MainLoop : Action {

    private val io: BaseIO = ioInstance()

    //    val liftTriggerLatch = LatchedBoolean()
    var timerRunning = false

    override fun firstCycle() {
        io.config.apply {
            enableTelemetryOutput = false
            enableDriveEncoderInput = false
            enableLiftEncoderInput = false
            enableGyroInput = false
            enableLimelightInput = true
            enableDriverInput = true
            enableOperatorInput = true
        }
        Lift.feedforwardEnabled = false
        Lift.setpointInches = 0.0
        Lift.isManual = true
        io.driveRampRate = 0.15
    }

    override fun shouldFinish(): Boolean {
        return false
    }

    override fun update() {
        io.driverInput.apply {

            val xSpeed = applyDeadband(-leftY, 1.0, 0.2)
            val zRotation = applyDeadband(rightX, 1.0, 0.2)
            val isQuickTurn = leftBumper == HeldDown
            Drive.updateCurvatureDrive(xSpeed, zRotation, isQuickTurn)

            when (rightBumper) {
                Pressed -> io.limelightMode = LimelightMode.Vision
                Released -> io.limelightMode = LimelightMode.Driver
                else -> Unit
            }
            val wantAligning = rightBumper == HeldDown && !isQuickTurn
            Limelight.updateDriveAlignment(wantAligning, xSpeed)

            when {
                leftTrigger > 0.2 -> updatePassthrough(-leftTrigger)
                rightTrigger > 0.2 -> updatePassthrough(rightTrigger)
                else -> updatePassthrough(0.0)
            }
            if (bButton == Pressed) io.outtakeSpeed = 1.0

            when {
                aButton == Pressed -> if (!io.pushing) {
                    io.grabbing = false
                    if (!timerRunning) {
                        timerRunning = true
                        Looper.add(sequential {
                            +wait(0.3)
                            +runOnce {
                                io.invertPushing()
                                timerRunning = false
                            }
                        })
                    }

                } else io.invertPushing()

                xButton == Pressed -> {
                    io.invertGrabbing()
                    io.pushing = false
                }
            }
        }

        io.operatorInput.apply {

            io.liftControlMode = ControlMode.PercentOutput
            io.liftDemand = applyDeadband(leftY, 1.0, 0.2)
            io.liftFeedforward = -0.12

            /*if (abs(leftY) > 0.2) {
                Lift.manualSpeed = applyDeadband(leftY, 1.0, 0.2)
                Lift.isManual = true
            } else Lift.manualSpeed = 0.0

            Lift.updateManualControl()

            when (Pressed) {
                rightBumper -> Lift.increaseSetpoint()
                leftBumper -> Lift.decreaseSetpoint()
                yButton -> {
                    Lift.setpointType = HatchCargo.Hatch
                    Lift.setpointInches = Lift.getCoolSetpoint()
                    Lift.isManual = false
                }
                bButton -> {
                    Lift.setpointType = HatchCargo.Cargo
                    Lift.setpointInches = Lift.getCoolSetpoint()
                    Lift.isManual = false
                }
                xButton -> {
                    Lift.setpointInches = FieldConstants.kHatch1Height - LiftConstants.kHatchIntakeHeight
                    Lift.isManual = false
                }
                aButton -> {
                    Lift.setpointInches = LiftConstants.kHomeHeightInches
                    Lift.isManual = false
                }
                startButton -> {
                    Lift.feedforwardEnabled = !Lift.feedforwardEnabled
                }
                else -> Unit
            }

            if (liftTriggerLatch.update(leftTrigger > 0.2)) {
                Lift.setpointInches = FieldConstants.kCargo2Height - 12.0
                Lift.isManual = false
            }

            if (Lift.isManual) Lift.updateManualControl()
            else Lift.updatePositionControl()*/
        }
    }

    fun updatePassthrough(speed: Double) {
        io.outtakeSpeed = speed * 0.8
        io.conveyorSpeed = speed * 0.9
        io.intakeSpeed = speed
    }
}