package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.SPI

object Navx : Subsystem() {
    val ahrs = AHRS(SPI.Port.kMXP)
    var calibrated = false
    var yaw = 0.0

    override fun onMeasure(dt: Double) {
        if (calibrated) {
            yaw = ahrs.fusedHeading.toDouble()
        }
    }
}