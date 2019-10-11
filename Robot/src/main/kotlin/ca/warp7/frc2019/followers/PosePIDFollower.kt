package ca.warp7.frc2019.followers

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.geometry.radians
import ca.warp7.frc.trajectory.TrajectoryController
import ca.warp7.frc.trajectory.TrajectoryFollower
import ca.warp7.frc.trajectory.TrajectoryState
import ca.warp7.frc2019.subsystems.Drive

class PosePIDFollower : TrajectoryFollower {

    companion object {
        const val kX = 5.0
        const val kY = 5.0
        const val kTheta = 1.0
    }

    override fun updateTrajectory(
            controller: TrajectoryController,
            setpoint: TrajectoryState,
            error: Pose2D
    ) {
        val adjustedLinear = setpoint.v +
                kX * error.translation.x

        val adjustedAngular = setpoint.w +
                setpoint.v * kY * error.translation.y +
                kTheta * error.rotation.radians

        Drive.setAdjustedVelocity(adjustedLinear, adjustedAngular)
    }
}