package ca.warp7.frc2019.constants

object LiftConstants {
    const val kMaster = ElectricalConstants.kLiftMasterTalonId
    const val kFollower = ElectricalConstants.kLiftFollowerVictorId
    const val kMaxAcceleration = 0.0 //TODO find actual max acceleration
    const val kMaxVelocityInchesPerSecond = 74 //TODO find out if this is true
}