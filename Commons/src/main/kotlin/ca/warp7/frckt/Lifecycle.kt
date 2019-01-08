@file:Suppress("unused")

package ca.warp7.frckt

import ca.warp7.action.IAction
import ca.warp7.action.impl.ActionMode
import edu.wpi.first.wpilibj.Timer
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal object Lifecycle {

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

    fun mainLoop() {
        val time = Timer.getFPGATimestamp()
        val dt = time - previousTime
        previousTime = time

        controllers.forEach { if (it.controllerEnabled) collectControllerData(it.data, it.controller) }
        inputSystems.forEach { it.onMeasure(dt) }

        if (robotEnabled) {
            controlLoop?.periodic()
            subsystems.forEach {
                it.state?.update()
                it.onOutput()
            }
        }

        outContent.apply {
            toString().trim().also { if (it.isNotEmpty()) originalOut.println(it) }
        }.reset()

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


