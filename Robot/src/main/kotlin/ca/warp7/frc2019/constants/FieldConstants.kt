package ca.warp7.frc2019.constants

object FieldConstants {
    private const val centerToCenterInches = 28.0
    const val firstCargoBayCenterHeightInches = 25.5
    const val secondCargoBayCenterHeightInches = firstCargoBayCenterHeightInches + centerToCenterInches
    const val thirdCargoBayCenterHeightInches = secondCargoBayCenterHeightInches + centerToCenterInches

    private const val firstHatchPortCenterHeightInches = 19.0
    const val secondHatchPortCenterHeightInches = firstHatchPortCenterHeightInches + centerToCenterInches
    const val thirdHatchPortCenterHeightInches = secondHatchPortCenterHeightInches + centerToCenterInches
}