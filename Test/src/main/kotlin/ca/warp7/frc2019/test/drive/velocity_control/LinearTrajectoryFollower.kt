package ca.warp7.frc2019.test.drive.velocity_control

import ca.warp7.actionkt.Action
import edu.wpi.first.wpilibj.Timer

@Suppress("MemberVisibilityCanBePrivate")
class LinearTrajectoryFollower : Action {
    val trajectory = LinearTrajectory(25.0)
    val moments = trajectory.moments
    val totalTime = moments.last().t
    var currentTime = 0.0
    var i = 0

    override fun update() {
        currentTime = Timer.getFPGATimestamp()
        while (i < moments.size && moments[i].t < currentTime) i++
    }

    override val shouldFinish: Boolean
        get() = currentTime > totalTime || i >= moments.size
}