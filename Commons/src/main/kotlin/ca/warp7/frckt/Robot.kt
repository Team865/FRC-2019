package ca.warp7.frckt

import ca.warp7.action.IAction
import ca.warp7.action.impl.Queue
import ca.warp7.frc.ControlLoop
import ca.warp7.frc.Input
import ca.warp7.frc.RobotController
import ca.warp7.frc.RobotRuntime.RT
import ca.warp7.frc.Subsystem

fun registerInput(input: Input) = RT.registerInput(input)
fun startRobot(loopsPerSecond: Int = 50) = RT.start(loopsPerSecond)
fun startRobotControls(controlLoop: ControlLoop?) = RT.initControls(controlLoop)
fun disableRobot() = RT.disableOutputs()
fun robotController(port: Int, active: Boolean): RobotController = RT.getController(port, active)
fun limit(value: Double, lim: Double): Double = Math.max(-1 * Math.abs(lim), Math.min(value, Math.abs(lim)))
fun action(factory: IAction.API.() -> IAction): IAction = factory.invoke(Queue())
fun autonomousMode(factory: IAction.API.() -> IAction): () -> IAction = { action(factory) }
fun runRobotAutonomous(mode: () -> IAction, timeout: Double = 15.0) = RT.initAuto(mode, timeout)
fun IAction.API.setState(subsystem: Subsystem, state: IAction): IAction.API = exec { subsystem.state = state }