package ca.warp7.frc2019.auton

import ca.warp7.actionkt.Action
import ca.warp7.frc.PID
import ca.warp7.frc.PIDValues

class TestPIDValues(pidValues: PIDValues, errorEpsilon: Double, dErrorEpsilon: Double, setpoint: Int) : Action {
    val pid = PID(pidValues, errorEpsilon = errorEpsilon, dErrorEpsilon = dErrorEpsilon, maxOutput = Double.POSITIVE_INFINITY)

    override fun update() {
        println(1)
    }
}