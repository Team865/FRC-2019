package ca.warp7.frckt

import ca.warp7.action.IAction
import ca.warp7.action.impl.Queue
import ca.warp7.frc.ControlLoop
import ca.warp7.frc.RobotRuntime.RT
import ca.warp7.frc.Subsystem

fun startControlLoop(controlLoop: ControlLoop?) = RT.initControls(controlLoop)
fun disableRobot() = RT.disableOutputs()

fun action(factory: IAction.API.() -> IAction): IAction = factory.invoke(Queue())
fun autonomousMode(factory: IAction.API.() -> IAction): () -> IAction = { action(factory) }
fun runRobotAutonomous(mode: () -> IAction, timeout: Double = 15.0): IAction = RT.initAuto(mode, timeout)
fun IAction.API.setState(subsystem: Subsystem, state: IAction): IAction.API = exec { subsystem.state = state }