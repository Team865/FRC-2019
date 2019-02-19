package ca.warp7.frc

import ca.warp7.actionj.impl.ActionMode
import ca.warp7.actionkt.*
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.livewindow.LiveWindow

internal object CommonRobot {

    init {
        Thread.currentThread().name = "Robot"
    }

    private val subsystems: MutableSet<Subsystem> = mutableSetOf()

    internal val robotDriver = RobotControllerImpl()
    internal val robotOperator = RobotControllerImpl()

    private val xboxDriver = XboxController(0)
    private val xboxOperator = XboxController(1)

    private var controllerMode = 0
    private val fmsAttached = DriverStation.getInstance().isFMSAttached

    private var previousTime = 0.0
    private var robotEnabled = false
    private var crashed = false

    private var autoRunner: Action = runOnce { }

    private var controlLoop: RobotControlLoop? = null

    fun setControllerMode(mode: ControllerMode) {
        controllerMode = mode.value
    }

    fun addSubsystem(subsystem: Subsystem) {
        subsystems.add(subsystem)
    }

    /**
     * Set the control loop
     */
    fun setLoop(loop: RobotControlLoop) {
        autoRunner.stop()
        robotEnabled = true
        loop.setup()
        controlLoop = loop
    }

    /**
     * Runs the loop with a try-catch statement
     */
    fun pauseOnCrashPeriodicLoop() {
        if (!crashed) {
            try {
                periodicLoop()
            } catch (e: Throwable) {
                crashed = true
                e.printStackTrace()
                //originalErr.println("ERROR LOOP ENDED\n${e.message}")
            }
        }
    }

    /**
     * Runs a periodic loop that collects inputs, update the autonomous
     * routine and controller loop, process subsystem states, send output
     * signals, and send telemetry data
     */
    private fun periodicLoop() {
        // Collect controller data
        when (controllerMode) {
            0 -> {
                robotDriver.updateWith(xboxDriver)
                robotOperator.updateWith(xboxOperator)
            }
            1 -> {
                robotDriver.updateWith(xboxDriver)
                robotOperator.reset()
            }
            2 -> {
                robotDriver.reset()
                robotOperator.updateWith(xboxDriver)
            }
        }
        // Check to switch controllers
        if (!fmsAttached && robotDriver.backButton == ControllerState.Pressed) {
            controllerMode = (controllerMode + 1) % 3
        }
        // Calculate exact loop period for measurements
        val time = Timer.getFPGATimestamp()
        val dt = time - previousTime
        previousTime = time
        // Get inputs from sensors
        subsystems.forEach { it.onMeasure(dt) }
        // Check for enabled state
        if (robotEnabled) {
            // Update the control loop
            controlLoop?.periodic()
            // Update subsystem state and do output, stopping the state if it wants to
            subsystems.forEach { it.updateState() }
        }
        // Send data to Shuffleboard
        subsystems.forEach {
            it.putIfNonEmpty("Current State", it.stateName)
            it.onPostUpdate()
        }
    }

    fun disableOutputs() {
        LiveWindow.disableAllTelemetry()
        autoRunner.stop()
        robotEnabled = false
        subsystems.forEach {
            it.stopState()
            it.onDisabled()
        }
    }

    fun runAutonomous(mode: () -> Action, timeout: Double): Action = ActionMode.createRunner(
            actionTimer { Timer.getFPGATimestamp() }, 20.0, timeout, mode().javaAction, true)
            .ktAction.also {
        autoRunner = it
        robotEnabled = true
        it.start()
    }
}


