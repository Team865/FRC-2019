package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc.geometry.Rotation2D
import ca.warp7.frc.geometry.fromRadians
import ca.warp7.frc.geometry.radians
import ca.warp7.frc2019.constants.InfrastructureConstants
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.PowerDistributionPanel
import edu.wpi.first.wpilibj.SPI

object Infrastructure : Subsystem() {
    private val ahrs = AHRS(SPI.Port.kMXP)
    private val pdp = PowerDistributionPanel(InfrastructureConstants.kPDPModule)

    var ahrsCalibrated = false
    var fusedHeading = 0.0
    var pitch = 0.0

    var yaw: Rotation2D = Rotation2D.identity
    private var pYaw = Rotation2D.identity
    var yawRate = 0.0

    override fun onMeasure(dt: Double) {
        if (!ahrsCalibrated && !ahrs.isCalibrating) ahrsCalibrated = true
        if (ahrsCalibrated) {
            pitch = Math.toRadians(ahrs.pitch.toDouble())
            fusedHeading = Math.toRadians(ahrs.fusedHeading.toDouble())
            pYaw = yaw
            yaw = Rotation2D.fromRadians(fusedHeading)
            yawRate = (pYaw - yaw).radians / dt
        }
    }

    override fun onPostUpdate() {
        /*
        put("ahrsCalibrated", ahrsCalibrated)
        put("Yaw", fusedHeading)
        put("Pitch", pitch)
        put(pdp)
        */
    }
}