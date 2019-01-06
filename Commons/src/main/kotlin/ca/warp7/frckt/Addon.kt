package ca.warp7.frckt

import ca.warp7.action.IAction
import ca.warp7.frc.ControlLoop


fun runRobot() = Unit // TODO
fun mainLoop() = Unit
fun stopAutonomous() = Unit

fun sandstorm(mode: () -> IAction, exitCondition: () -> Boolean, controlLoop: ControlLoop, timeout: Double = 15.0) {
    runRobotAutonomous(mode, timeout)
    startControlLoop(object : ControlLoop {
        override fun setup() = Unit
        override fun periodic() {
            if (exitCondition.invoke()) {
                stopAutonomous()
                startControlLoop(controlLoop)
            }
        }
    })
}

val driver = robotController(0, true)
val operator = robotController(1, true)