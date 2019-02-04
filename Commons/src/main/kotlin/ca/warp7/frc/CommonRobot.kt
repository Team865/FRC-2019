package ca.warp7.frc

import ca.warp7.actionj.impl.ActionMode
import ca.warp7.actionkt.*
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal object CommonRobot {

    init {
        Thread.currentThread().name = "Robot"
    }

    val subsystems: MutableSet<Subsystem> = mutableSetOf()

    val robotDriver = RobotController()
    val robotOperator = RobotController()

    private val xboxDriver = XboxController(0)
    private val xboxOperator = XboxController(1)

    private val fmsAttached = DriverStation.getInstance().isFMSAttached
    private var controllerMode = 0

    private val originalOut = System.out
    private val originalErr = System.err
    private val outContent = ByteArrayOutputStream().also { System.setOut(PrintStream(it)) }
    private val errContent = ByteArrayOutputStream().also { System.setErr(PrintStream(it)) }

    private var previousTime = 0.0
    private var robotEnabled = false
    private var crashed = false

    private var autoRunner: Action = runOnce { }

    var controlLoop: RobotControlLoop? = null
        set(value) {
            autoRunner.stop()
            robotEnabled = true
            value?.setup()
            field = value
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
                originalErr.println("ERROR LOOP ENDED\n${e.message}")
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
                collectControllerData(robotDriver.data, xboxDriver)
                collectControllerData(robotOperator.data, xboxOperator)
            }
            1 -> {
                collectControllerData(robotDriver.data, xboxDriver)
                resetControllerData(robotOperator.data)
            }
            2 -> {
                resetControllerData(robotDriver.data)
                collectControllerData(robotOperator.data, xboxDriver)
            }
        }
        // Check to switch controllers
        if (!fmsAttached || robotDriver.data.backButton == ControllerState.Pressed) {
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
            subsystems.forEach {
                it.apply {
                    // Check if the current state wants to finish before updating
                    if (currentState?.shouldFinish == true) {
                        // Stop and remove the current state
                        currentState?.stop()
                        currentState = null
                    } else {
                        // Update the current state
                        currentState?.update()
                    }
                    // To subsystem output
                    onOutput()
                }
            }
        }
        // Send data to Shuffleboard
        subsystems.forEach {
            Shuffleboard.getTab(it::class.java.simpleName).apply {
                // Show the current state in the appropriate tab
                add("Current State",
                        if (it.currentState != null) it.currentState!!::class.java.simpleName else "None")
                        .withWidget(BuiltInWidgets.kTextView).withPosition(0, 0)
                // Update the rest to shuffleboard
                it.onUpdateShuffleboard(this)
            }
        }
        // Flush the standard output
        outContent.apply {
            toString().trim().also { if (it.isNotEmpty()) originalOut.println(it) }
        }.reset()
        // Flush the standard error adding ERROR before it
        errContent.apply {
            toString().split(System.lineSeparator().toRegex()).forEach {
                if (it.isNotEmpty()) originalErr.println("ERROR $it")
            }
        }.reset()
    }

    fun disableOutputs() {
        autoRunner.stop()
        robotEnabled = false
        subsystems.forEach { it.onDisabled() }
    }

    fun runAutonomous(mode: () -> Action, timeout: Double): Action = ActionMode.createRunner(
            actionTimer { Timer.getFPGATimestamp() }, 20.0, timeout, mode().javaAction, true)
            .ktAction.also {
        autoRunner = it
        robotEnabled = true
        it.start()
    }
}


