package ca.warp7.frc2019.actions

@Suppress("UNUSED_PARAMETER", "FunctionName")
fun DriveForDistance(
        distanceInFeet: Double,
        isBackwards: Boolean = distanceInFeet < 0,
        velocityScale: Double = 1.0,
        linearKp: Double = 0.0,
        angularKp: Double = 500.0,
        kA: Double = 1.0 / 30
) = DriveTrajectory(distanceInFeet / 12.0)