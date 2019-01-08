@file:Suppress("unused")

package ca.warp7.frckt

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets.*
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer

class ShuffleboardBuilder {
}

private typealias SB = ShuffleboardBuilder

fun widgets(builtInWidgets: BuiltInWidgets) {
    when (builtInWidgets) {

        kTextView -> TODO()
        kNumberSlider -> TODO()
        kNumberBar -> TODO()
        kDial -> TODO()
        kGraph -> TODO()
        kBooleanBox -> TODO()
        kToggleButton -> TODO()
        kToggleSwitch -> TODO()
        kVoltageView -> TODO()
        kPowerDistributionPanel -> TODO()
        kComboBoxChooser -> TODO()
        kSplitButtonChooser -> TODO()
        kEncoder -> TODO()
        kSpeedController -> TODO()
        kCommand -> TODO()
        kPIDCommand -> TODO()
        kPIDController -> TODO()
        kAccelerometer -> TODO()
        k3AxisAccelerometer -> TODO()
        kGyro -> TODO()
        kRelay -> TODO()
        kDifferentialDrive -> TODO()
        kMecanumDrive -> TODO()
        kCameraStream -> TODO()
    }
}

fun SB.textView() = Unit
fun SB.numberSlider() = Unit
fun SB.numberBar() = Unit
fun SB.dial() = Unit
fun SB.graph() = Unit
fun SB.booleanBox() = Unit
fun SB.toggleButton() = Unit
fun SB.toggleSwitch() = Unit
fun SB.voltageView() = Unit
fun SB.powerDistributionPanel() = Unit
fun SB.comboBoxChooser() = Unit
fun SB.splitButtonChooser() = Unit
fun SB.encoder() = Unit
fun SB.speedController() = Unit
fun SB.command() = Unit
fun SB.pidCommand() = Unit
fun SB.pidController() = Unit
fun SB.accelerometer() = Unit
fun SB.threeAxisAccelerometer() = Unit
fun SB.gyro() = Unit
fun SB.relay() = Unit
fun SB.differentialDrive() = Unit
fun SB.mecanumDrive() = Unit
fun SB.cameraStream() = Unit
fun SB.noWidget() = Unit

fun ShuffleboardContainer.sendAll(block: ShuffleboardBuilder.() -> Unit) = Unit