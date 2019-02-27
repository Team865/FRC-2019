package ca.warp7.frc

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.livewindow.LiveWindow

internal object CommonRobot {

    private val subsystems: MutableSet<Subsystem> = mutableSetOf()

    internal val robotDriver = RobotControllerImpl()
    internal val robotOperator = RobotControllerImpl()

    private val xboxDriver = XboxController(0)
    private val xboxOperator = XboxController(1)

    private var controllerMode = 0
    private val driverStation: DriverStation = DriverStation.getInstance()
    private val fmsAttached = driverStation.isFMSAttached

    private var previousTime = 0.0
    private var robotEnabled = false
    private var crashed = false

    fun setControllerMode(mode: ControllerMode) {
        controllerMode = mode.value
    }

    fun addSubsystem(subsystem: Subsystem) {
        subsystems.add(subsystem)
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
        if (driverStation.isNewControlData) when (controllerMode) {
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
        if (!fmsAttached && robotDriver.backButton == ControllerState.Pressed)
            controllerMode = (controllerMode + 1) % 3
        // Calculate exact loop period for measurements
        val time = Timer.getFPGATimestamp()
        val dt = time - previousTime
        previousTime = time
        // Get inputs from sensors
        subsystems.forEach { it.onMeasure(dt) }
        // Check for enabled state, and
        // update subsystem state and do output, stopping the state if it wants to
        if (robotEnabled) subsystems.forEach { it.updateState() }
        // Send data to Shuffleboard
        subsystems.forEach { it.onPostUpdate() }
    }

    fun disableOutputs() {
        LiveWindow.disableAllTelemetry()
        robotEnabled = false
        subsystems.forEach {
            it.stopState()
            it.onDisabled()
        }
    }

    fun enable() {
        robotEnabled = true
    }
}


