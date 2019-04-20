package ca.warp7.frc2019.subsystems

import ca.warp7.frc.control.Subsystem
import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.networktables.NetworkTableEntry
import edu.wpi.first.networktables.NetworkTableInstance

object Limelight : Subsystem() {
    private val table: NetworkTable = NetworkTableInstance.getDefault().getTable("limelight")
    private val tv: NetworkTableEntry = table.getEntry("tv")
    private val tx: NetworkTableEntry = table.getEntry("tx")
    private val ty: NetworkTableEntry = table.getEntry("ty")
    private val ta: NetworkTableEntry = table.getEntry("ta")
    private val tl: NetworkTableEntry = table.getEntry("tl")
    private val camMode: NetworkTableEntry = table.getEntry("camMode")
    private val ledMode: NetworkTableEntry = table.getEntry("ledMode")

    var hasTarget = false
    var x = 0.0
    var y = 0.0
    var area = 0.0
    var latency = 0.0

    var connected = false

    var isDriver = false
//        set(value) {
//            //if (field != value) {
//                if (value) {
//                    camMode.setDouble(1.0)
//                    ledMode.setDouble(1.0)
//                } else {
//                    camMode.setDouble(0.0)
//                    ledMode.setDouble(0.0)
//                }
//            //}
//            field = value
//        }

    override fun onOutput() {
        (if (isDriver) 1.0 else 0.0).also{
            camMode.setDouble(it)
            ledMode.setDouble(it)
        }
    }
    override fun onMeasure(dt: Double) {
        if (!connected && tv.exists()) connected = true
        if (connected && !isDriver) {
            hasTarget = tv.getDouble(0.0).toInt() == 1
            if (hasTarget) {
                x = tx.getDouble(0.0)
                y = ty.getDouble(0.0)
                area = ta.getDouble(0.0)
                latency = tl.getDouble(0.0)
            }
        }
    }

    override fun onPostUpdate() {
        /*
        graph("x", x)
        graph("y", y)
        graph("area", area)
        */
    }
}