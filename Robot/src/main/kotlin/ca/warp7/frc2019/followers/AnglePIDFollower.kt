package ca.warp7.frc2019.followers

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.trajectory.TrajectoryController
import ca.warp7.frc.trajectory.TrajectoryFollower
import ca.warp7.frc.trajectory.TrajectoryState
import ca.warp7.frc2019.subsystems.Drive

class AnglePIDFollower : TrajectoryFollower {

    companion object {
        const val kP = 5.0
    }

    override fun updateTrajectory(
            controller: TrajectoryController,
            setpoint: TrajectoryState,
            error: Pose2D
    ) {
        val adjustedAngular = setpoint.w +
                setpoint.v * kP * error.rotation.radians()

        Drive.setAdjustedVelocity(setpoint.v, adjustedAngular)
    }
}