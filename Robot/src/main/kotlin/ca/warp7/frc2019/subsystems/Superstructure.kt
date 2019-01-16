package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import ca.warp7.frc2019.constants.SuperstructureConstants
import edu.wpi.first.wpilibj.Compressor
import edu.wpi.first.wpilibj.PowerDistributionPanel
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

object Superstructure : Subsystem() {

    val compressor = Compressor(SuperstructureConstants.kCompressorModule)
    private val powerDistributionPanel = PowerDistributionPanel(SuperstructureConstants.kPDPModule)

    override fun onDisabled() {
        compressor.closedLoopControl = false
    }

    override fun onOutput() {
    }

    override fun onUpdateShuffleboard(container: ShuffleboardContainer) {
        container.add("pdp", powerDistributionPanel).withWidget(BuiltInWidgets.kPowerDistributionPanel)
    }
}