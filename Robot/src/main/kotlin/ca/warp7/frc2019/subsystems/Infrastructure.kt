package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.InfrastructureConstants
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.PowerDistributionPanel
import edu.wpi.first.wpilibj.SPI

object Infrastructure : Subsystem() {
    //private val compressor = Compressor(InfrastructureConstants.kCompressorModule)
    private val ahrs = AHRS(SPI.Port.kMXP)
    private val pdp = PowerDistributionPanel(InfrastructureConstants.kPDPModule)

    var ahrsCalibrated = false
    var yaw = 0.0
    var pitch = 0.0

    var startCompressor = true

    override fun onDisabled() {
        //compressor.stop()
        startCompressor = true
    }

    override fun onOutput() {
        if (startCompressor) {
            //compressor.start()
            startCompressor = false
        }
    }

    override fun onMeasure(dt: Double) {
        if (!ahrsCalibrated && !ahrs.isCalibrating) ahrsCalibrated = true
        //if (compressor.pressureSwitchValue && !compressor.enabled()) startCompressor = true
        if (ahrsCalibrated) {
            yaw = Math.toRadians(ahrs.fusedHeading.toDouble())
            pitch = Math.toRadians(ahrs.pitch.toDouble())
        }
    }

    override fun onPostUpdate() {
        //put(compressor)
        put(pdp)
        put("ahrsCalibrated", ahrsCalibrated)
        put("Yaw", yaw)
        put("Pitch", pitch)
    }
}