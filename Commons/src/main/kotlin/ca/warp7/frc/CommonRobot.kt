@file:Suppress("unused")

package ca.warp7.frc

import ca.warp7.actionj.impl.ActionMode
import ca.warp7.actionkt.*
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal object CommonRobot {

    val subsystems: MutableSet<Subsystem> = mutableSetOf()
    val inputSystems: MutableSet<InputSystem> = mutableSetOf()
    val controllers: MutableSet<RobotController> = mutableSetOf()

    var controlLoop: RobotControlLoop? = null
        set(value) {
            autoRunner.stop()
            robotEnabled = true
            field = value
        }

    private val outContent = ByteArrayOutputStream()
    private val errContent = ByteArrayOutputStream()
    private val originalOut = System.out
    private val originalErr = System.err

    private var previousTime = 0.0
    private var robotEnabled = false
    private var crashed = false

    private var autoRunner: Action = runOnce { }

    init {
        Thread.currentThread().name = "Robot"
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))
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
        controllers.forEach { if (it.active) collectControllerData(it.data, it.controller) }
        // Calculate exact loop period for measurements
        val time = Timer.getFPGATimestamp()
        val dt = time - previousTime
        previousTime = time
        // Get inputs from sensors
        inputSystems.forEach { it.onMeasure(dt) }
        // Check for enabled state
        if (robotEnabled) {
            // Update the control loop
            controlLoop?.periodic()
            // Update subsystem state and do output, stopping the state if it wants to
            subsystems.forEach {
                it.apply {
                    // Check if the current state wants to finish before updating
                    if (currentState?.shouldFinish() == true) {
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
        inputSystems.forEach {
            Shuffleboard.getTab(it::class.java.simpleName).apply {
                // Show the current state in the appropriate tab
                if (it is Subsystem) add("Current State",
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
    }

    fun runAutonomous(mode: () -> Action, timeout: Double): Action = ActionMode.createRunner(
            actionTimer { Timer.getFPGATimestamp() },
            20.0,
            timeout,
            mode.invoke().javaAction,
            true)
            .ktAction.also {
        autoRunner = it
        robotEnabled = true
        it.start()
    }
}


