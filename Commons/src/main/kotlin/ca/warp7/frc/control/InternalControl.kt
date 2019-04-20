package ca.warp7.frc.control

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.livewindow.LiveWindow

internal object InternalControl {

    private val subsystems: MutableSet<Subsystem> = mutableSetOf()

    internal val robotDriver = RobotControllerImpl()
    internal val robotOperator = RobotControllerImpl()

    val xboxDriver = XboxController(0)
    val xboxOperator = XboxController(1)

    private var controllerMode = 0
    private val driverStation: DriverStation = DriverStation.getInstance()
    private val fmsAttached: Boolean = driverStation.isFMSAttached

    private var previousTime = 0.0
    private var robotEnabled = false
    private var crashed = false

    fun setControllerMode(mode: ControllerMode) {
        controllerMode = mode.value
    }

    fun addSubsystem(subsystem: Subsystem) {
        subsystems.add(subsystem)
    }

    fun disableOutputs() {
        LiveWindow.disableAllTelemetry()
        subsystems.forEach { it.stopState() }
        robotEnabled = false
    }

    fun enable() {
        robotEnabled = true
    }

    private fun updateControllers() {
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
        if (!fmsAttached && robotDriver.backButton == ControllerState.Pressed)
            controllerMode = (controllerMode + 1) % 3
    }

    fun haltingPeriodicLoop() {
        if (!crashed) {
            try {
                updateControllers()
                val time = Timer.getFPGATimestamp()
                val dt = time - previousTime
                previousTime = time
                subsystems.forEach { it.onMeasure(dt) }
                if (robotEnabled) subsystems.forEach { it.updateState() }
                //subsystems.forEach { it.onPostUpdate() }
            } catch (e: Throwable) {
                crashed = true
                print("ERROR LOOP ENDED")
                e.printStackTrace()
            }
        }
    }
}


