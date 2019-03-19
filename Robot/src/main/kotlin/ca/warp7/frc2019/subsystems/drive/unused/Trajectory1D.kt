package ca.warp7.frc2019.subsystems.drive.unused

import ca.warp7.frc2019.constants.DriveConstants
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.withSign

class Trajectory1D(val dt: Double, val distance: Double, val startVel: Double = 0.0, val endVel: Double = 0.0) {
    data class TrajectoryPoint1D(val time: Double, val position: Double, val velocity: Double, val acceleration: Double)

    var maxVelocity = DriveConstants.kMaxVelocity
    var maxAcceleration = DriveConstants.kMaxAcceleration

    var trajectory: List<TrajectoryPoint1D> = emptyList()

    init {
        //forwards pass
        val forwardsPass = mutableListOf(TrajectoryPoint1D(0.0, 0.0, startVel, 0.0))
        while (forwardsPass.last().position <= distance) {
            val last = forwardsPass.last()
            forwardsPass += last.also {
                TrajectoryPoint1D(
                        it.time + dt,
                        it.position + min(abs(it.velocity), maxVelocity).withSign(it.velocity),
                        min(abs(it.velocity + it.acceleration), maxVelocity).withSign(it.velocity + it.acceleration),
                        -maxAcceleration
                )
            }
        }

        //backwards pass
        val backwardsPass = mutableListOf(TrajectoryPoint1D(0.0, distance, endVel, 0.0))
        while (backwardsPass.last().position >= 0.0) {
            val last = backwardsPass.last()
            backwardsPass += last.also {
                TrajectoryPoint1D(
                        it.time - dt,
                        it.position + maxVelocity,
                        min(abs(it.velocity + it.acceleration), maxVelocity).withSign(it.velocity + it.acceleration),
                        -maxAcceleration
                )
            }
        }
        backwardsPass.reverse()
        // merge passes
        forwardsPass.forEach {
//maxV/(1+0.5*L*curvature)
        }
    }

    fun doThing(curPosition: Double, curVelocity: Double): Double = when {
        distance - decelerationDistance(curVelocity) <= curPosition ->
            trajectory.minBy { abs(it.position - decelerationDistance(curVelocity)) }!!.velocity
        curPosition > distance ->
            curVelocity - maxAcceleration
        else ->
            trajectory.minBy { abs(it.position - curPosition) }!!.velocity
    }

    fun decelerationTime(curVelocity: Double): Double = (endVel - curVelocity) / (-maxAcceleration)
    fun decelerationDistance(curVelocity: Double): Double = (endVel - curVelocity).pow(2) / (-maxAcceleration * 2)

}