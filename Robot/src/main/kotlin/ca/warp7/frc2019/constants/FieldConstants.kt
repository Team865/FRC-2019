package ca.warp7.frc2019.constants

@Suppress("unused")
object FieldConstants {
    private const val centerToCenterInches = 28.0
    const val kCargo1Height = 25.5 // center
    const val kCargo2Height = 53.5 // kCargo1Height + centerToCenterInches
    const val kCargo3Height = 81.5 // kCargo2Height + centerToCenterInches

    private const val firstHatchPortCenterHeightInches = 19.0
    const val secondHatchPortCenterHeightInches = 47.0 // firstHatchPortCenterHeightInches + centerToCenterInches
    const val thirdHatchPortCenterHeightInches = 75.0 //secondHatchPortCenterHeightInches + centerToCenterInches
}