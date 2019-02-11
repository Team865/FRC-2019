package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.InfrastructureConstants
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.Compressor
import edu.wpi.first.wpilibj.PowerDistributionPanel
import edu.wpi.first.wpilibj.SPI
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

object Infrastructure : Subsystem() {
    val compressor = Compressor(InfrastructureConstants.kCompressorModule)
    val ahrs = AHRS(SPI.Port.kMXP)
    private val powerDistributionPanel = PowerDistributionPanel(InfrastructureConstants.kPDPModule)

    var ahrsCalibrated = false
    var yaw = 0.0
    var pitch = 0.0

    override fun onDisabled() {
        compressor.stop()
    }

    override fun onMeasure(dt: Double) {
        if (ahrsCalibrated) {
            yaw = Math.toRadians(ahrs.fusedHeading.toDouble())
            pitch = Math.toRadians(ahrs.pitch.toDouble())
        }
    }

    override fun onOutput() {
    }

    override fun onUpdateShuffleboard(container: ShuffleboardContainer) {
        container.apply {
            add("pdp", powerDistributionPanel).withWidget(BuiltInWidgets.kPowerDistributionPanel)
            add("ahrsCalibrated", ahrsCalibrated)
            add("Yaw", yaw)
            add("Pitch", pitch)
        }
    }
}