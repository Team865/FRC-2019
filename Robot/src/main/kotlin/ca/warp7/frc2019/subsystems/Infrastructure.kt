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
    private val compressor = Compressor(InfrastructureConstants.kCompressorModule)
    private val powerDistributionPanel = PowerDistributionPanel(InfrastructureConstants.kPDPModule)
    val ahrs = AHRS(SPI.Port.kMXP)

    var calibrated = false
    var yaw = 0.0

    override fun onDisabled() {
        compressor.closedLoopControl = false
    }

    override fun onMeasure(dt: Double) {
        if (calibrated) {
            yaw = Math.toRadians(ahrs.fusedHeading.toDouble())
        }
    }

    override fun onOutput() {
    }

    override fun onUpdateShuffleboard(container: ShuffleboardContainer) {
        container.add("pdp", powerDistributionPanel).withWidget(BuiltInWidgets.kPowerDistributionPanel)
    }
}