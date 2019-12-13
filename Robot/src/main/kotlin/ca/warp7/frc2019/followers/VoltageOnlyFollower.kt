package ca.warp7.frc2019.followers

import ca.warp7.frc.geometry.Pose2D
import ca.warp7.frc.trajectory.TrajectoryController
import ca.warp7.frc.trajectory.TrajectoryState
import ca.warp7.frc2019.helper.acceleration
import ca.warp7.frc2019.helper.velocity
import ca.warp7.frc2019.subsystems.Drive

class VoltageOnlyFollower : TrajectoryFollower {
    override fun updateTrajectory(
            controller: TrajectoryController,
            setpoint: TrajectoryState,
            error: Pose2D
    ) {
        Drive.setChassisDynamics(setpoint.velocity, setpoint.acceleration)
        Drive.setVoltageOnly()
    }
}