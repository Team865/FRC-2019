package ca.warp7.frc2019.followers

import ca.warp7.frc.epsilonEquals
import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.trajectory.TrajectoryController
import ca.warp7.frc.trajectory.TrajectoryFollower
import ca.warp7.frc.trajectory.TrajectoryState
import ca.warp7.frc2019.subsystems.Drive
import kotlin.math.sqrt

/**
 * Equation 5.12 from https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
 */
class RamseteFollower : TrajectoryFollower {

    companion object {
        const val kBeta = 2.0  // Correction coefficient, β > 0
        const val kZeta = 0.7  // Damping coefficient, 0 < ζ < 1
    }

    override fun updateTrajectory(
            controller: TrajectoryController,
            setpoint: TrajectoryState,
            error: Pose2D
    ) {

        val linearSquared = setpoint.v * setpoint.v
        val angularSquared = setpoint.w * setpoint.w

        val correctionGain = 2.0 * kZeta * sqrt(kBeta * linearSquared + angularSquared) // k

        val angularError = error.rotation.radians()
        val sinRatio = if (angularError.epsilonEquals(0.0, 1E-2)) {
            1.0
        } else {
            error.rotation.sin / angularError
        }

        val adjustedLinear = setpoint.v * error.rotation.cos + // current linear velocity
                correctionGain * error.translation.x // forward error correction

        val adjustedAngular = setpoint.w + // current angular velocity
                correctionGain * angularError + // turn error correction
                setpoint.v * kBeta * sinRatio * error.translation.y // lateral correction

        Drive.setAdjustedVelocity(adjustedLinear, adjustedAngular)
    }
}