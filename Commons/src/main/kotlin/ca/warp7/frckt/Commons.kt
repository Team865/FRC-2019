package ca.warp7.frckt

fun runRobot() = Lifecycle.runRobot()

fun mainLoop() = Lifecycle.mainLoop()

fun disableRobot() = Lifecycle.disableOutputs()

fun setControlLoop(controlLoop: ControlLoop?) {
    Lifecycle.controlLoop = controlLoop
}

fun runAutonomous(mode: () -> Action, timeout: Double = 15.0): Action = Lifecycle.runAutonomous(mode, timeout)

fun limit(value: Double, lim: Double): Double = Math.max(-1 * Math.abs(lim), Math.min(value, Math.abs(lim)))

val driver = RobotController(0).also { Lifecycle.controllers.add(it) }
val operator = RobotController(1).also { Lifecycle.controllers.add(it) }