package ca.warp7.frc2019.constants

@Suppress("unused")
object LiftConstants {
    const val kMaster = ElectricalConstants.kLiftMasterTalonId
    const val kFollower = ElectricalConstants.kLiftFollowerVictorId

    private const val kRotationsPerTick = 4096
    private const val kDrumRadiusInches = 1.5
    private const val kDrumCircumfrence = 2 * kDrumRadiusInches * Math.PI
    const val kInchesPerTick = kRotationsPerTick * kDrumCircumfrence

    const val kHomeHeightInches = 0.0 //TODO fix this value

    const val kMaxBaseAcceleration = 0.0 //TODO find actual max acceleration
    const val kMaxVelocityInchesPerSecond = 74.0 //TODO find out if this is true

    const val kHallEffect = ElectricalConstants.kLiftHallEffectSensorDIO
}