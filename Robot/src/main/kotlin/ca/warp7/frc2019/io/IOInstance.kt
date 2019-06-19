package ca.warp7.frc2019.io

import edu.wpi.first.wpilibj.RobotBase

private var ioInstance: BaseIO? = null

fun ioInstance(): BaseIO {
    val instance = ioInstance
    if (instance == null) {
        val newInstance = if (RobotBase.isReal()) PhysicalIO() else SimulatedIO()
        ioInstance = newInstance
        return newInstance
    }
    return instance
}