package ca.warp7.frc2019.subsystems

import ca.warp7.frc.Subsystem
import edu.wpi.first.wpilibj.Compressor
import edu.wpi.first.wpilibj.PowerDistributionPanel
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer


object Electrical : Subsystem() {

    val compressor = Compressor(ElectricalConstants.compressorModule)
    private val powerDistributionPanel = PowerDistributionPanel(ElectricalConstants.pdpModule)

    override fun onDisabled() {
        compressor.closedLoopControl = false
    }

    override fun onOutput() {
    }

    override fun onUpdateShuffleboard(container: ShuffleboardContainer) {
        container.add("pdp", powerDistributionPanel).withWidget(BuiltInWidgets.kPowerDistributionPanel)
    }
}