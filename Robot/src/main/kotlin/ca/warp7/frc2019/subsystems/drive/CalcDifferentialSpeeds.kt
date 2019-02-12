package ca.warp7.frc2019.subsystems.drive

@Suppress("unused")
fun calcDifferentialSpeeds(xSpeed: Double, zRotation: Double): Pair<Double, Double> {
    val angularPower: Double = Math.abs(xSpeed) * zRotation

    var leftMotorOutput = angularPower + xSpeed
    var rightMotorOutput = angularPower - xSpeed

    val maxMagnitude = Math.max(Math.abs(leftMotorOutput), Math.abs(rightMotorOutput))

    leftMotorOutput /= maxMagnitude
    rightMotorOutput /= maxMagnitude


    return Pair(leftMotorOutput, rightMotorOutput)
}
