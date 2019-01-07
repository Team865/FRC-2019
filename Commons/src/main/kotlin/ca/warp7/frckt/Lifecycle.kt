@file:Suppress("unused")

package ca.warp7.frckt

import edu.wpi.first.wpilibj.Timer
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal object Lifecycle {

    val subsystems: MutableSet<Subsystem> = mutableSetOf()
    val inputSystems: MutableSet<InputSystem> = mutableSetOf()
    val controllers: MutableSet<RobotController> = mutableSetOf()

    private val outContent = ByteArrayOutputStream()
    private val errContent = ByteArrayOutputStream()
    private val originalOut = System.out
    private val originalErr = System.err

    var previousTime = 0.0
    var enabled = false

    var controlLoop: ControlLoop? = null

    fun runRobot() {
        Thread.currentThread().name = "Robot"
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))
    }

    fun mainLoop() {
        val time = Timer.getFPGATimestamp()
        val dt = time - previousTime
        previousTime = time

        controllers.forEach { if (it.enabled) collectControllerData(it.data, it.controller) }
        inputSystems.forEach { it.onMeasure(dt) }

        if (enabled) {
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
        enabled = false
    }
}


