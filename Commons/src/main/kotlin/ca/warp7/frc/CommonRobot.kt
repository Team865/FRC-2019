@file:Suppress("unused")

package ca.warp7.frc

import ca.warp7.actionj.IAction
import ca.warp7.actionj.impl.ActionMode
import ca.warp7.actionkt.Action
import ca.warp7.actionkt.NothingAction
import ca.warp7.actionkt.javaAction
import ca.warp7.actionkt.ktAction
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal object CommonRobot {

    val subsystems: MutableSet<Subsystem> = mutableSetOf()
    val inputSystems: MutableSet<InputSystem> = mutableSetOf()
    val controllers: MutableSet<RobotController> = mutableSetOf()

    var controlLoop: ControlLoop? = null
        set(value) {
            robotEnabled = true
            field = value
        }

    private val outContent = ByteArrayOutputStream()
    private val errContent = ByteArrayOutputStream()
    private val originalOut = System.out
    private val originalErr = System.err

    private var previousTime = 0.0
    private var robotEnabled = false

    private var autoRunner: Action = NothingAction()

    init {
        Thread.currentThread().name = "Robot"
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))
    }

    /**
     * Runs a periodic loop that collects inputs, update the autonomous
     * routine and controller loop, process subsystem states, send output
     * signals, and send telemetry data
     */
    fun mainLoop() {
        // Collect controller data
        controllers.forEach { if (it.active) collectControllerData(it.data, it.controller) }
        // Calculate exact loop peroid for measurements
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
                    // Check if there is a new wanted state that is not the same as the current state
                    if (wantedState != null && wantedState != currentState) {
                        // Change to the new state
                        currentState = wantedState
                        // Start the new state
                        currentState?.start()
                        // Remove the wanted state
                        wantedState = null
                    }
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
                if (it is Subsystem) add("Current State",
                        if (it.currentState != null) it.currentState!!::class.java.simpleName else "None")
                        .withWidget(BuiltInWidgets.kTextView).withPosition(0, 0)
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
            IAction.ITimer { Timer.getFPGATimestamp() },
            20.0,
            timeout,
            mode.invoke().javaAction,
            true).ktAction.also {
        autoRunner = it
        robotEnabled = true
        it.start()
    }
}


