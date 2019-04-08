package ca.warp7.frc.drive

import ca.warp7.frc.epsilonEquals
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.withSign

/**
 * Model of the kinematics and dynamics of a differential drive
 */
@Suppress("unused")
data class DifferentialDriveModel(
        val wheelRadius: Double, // m
        val wheelbaseRadius: Double, // m (effective wheelbase)
        val maxVelocity: Double, // m/s
        val maxAcceleration: Double, // m/s^2
        val maxFreeSpeed: Double, // m/s
        val speedPerVolt: Double, // m/s per V (free speed of transmission per volt)
        val torquePerVolt: Double, // N m per V (stall torque of transmission per volt)
        val frictionVoltage: Double, // V (voltage that breaks static friction)
        val linearInertia: Double, // kg (equivalent mass)
        val angularInertia: Double, // kg*m^2 (equivalent moment of inertia)
        val maxVoltage: Double, // V (maximum voltage of transmission)
        val angularDrag: Double = 1.0 // (N*m)/(m/s)
) {


    /**
     * Solves the forward kinematics of the drive train by converting the
     * speeds/accelerations of wheels on each side into linear and angular
     * speed/acceleration
     */
    fun solve(wheels: WheelState) = ChassisState(
            linear = (wheels.left + wheels.right) / 2.0,
            angular = (wheels.right - wheels.left) / (2 * wheelbaseRadius)
    )


    /**
     * Solves the inverse kinematics of the drive train by converting linear
     * and angular speed/acceleration into the speeds/accelerations of wheels
     * on each side
     */
    fun solve(chassis: ChassisState) = WheelState(
            left = chassis.linear - chassis.angular * wheelbaseRadius,
            right = chassis.linear + chassis.angular * wheelbaseRadius
    )


    /**
     * Calculates the maximum reachable linear and angular velocity based on the curvature.
     * The result is unsigned.
     *
     * The equations are derived from `w(r + L / 2) = far side velocity`. Assume far side goes 100%,
     * we replace it with max velocity and isolate for angular velocity.
     *
     * Then we rearrange `w = (right - left)/L` into `left = maxV - wL`, substitute it into
     * `v = (left + right) / 2`, and get the equation for max linear velocity
     *
     * L is double of wheelBaseRadius, so calculations are simplified here.
     *
     * If curvature is 0, we just return a ChassisState with no angular velocity.
     *
     * Future: Does this also work for acceleration???
     */
    fun signedMaxAtCurvature(curvature: Double, maxVel: Double = maxVelocity): ChassisState {
        if (curvature.epsilonEquals(0.0, 1E-9)) {
            return ChassisState(maxVel, angular = 0.0)
        }
        val angular = maxVel / (1 / abs(curvature) + wheelbaseRadius)
        val linear = maxVel - (angular * wheelbaseRadius)
        return ChassisState(linear, angular.withSign(curvature))
    }

    /**
     * Solves the results from above into wheel velocities
     */
    fun solvedMaxAtCurvature(curvature: Double) = solve(signedMaxAtCurvature(curvature))

    /**
     * Get the free speed obtained at a specified voltage
     */
    fun freeSpeedAtVoltage(voltage: Double): Double {
        return when {
            voltage > kEpsilon -> max(0.0, voltage - frictionVoltage) * speedPerVolt
            voltage < kEpsilon -> min(0.0, voltage + frictionVoltage) * speedPerVolt
            else -> 0.0
        }
    }

    /**
     * Get the motor torque for a specified speed and voltage
     */
    fun torqueForVoltage(speed: Double, voltage: Double): Double {
        // calculate the effective voltage (taking away the friction voltage)
        var effectiveVoltage = voltage
        when {
            speed > kEpsilon -> effectiveVoltage -= frictionVoltage
            speed < -kEpsilon -> effectiveVoltage += frictionVoltage
            voltage > kEpsilon -> effectiveVoltage = max(0.0, voltage - frictionVoltage)
            voltage < -kEpsilon -> effectiveVoltage = min(0.0, voltage + frictionVoltage)
            else -> return 0.0
        }
        // calculate torque based on the voltage left
        return torquePerVolt * (effectiveVoltage - speed / speedPerVolt)
    }

    /**
     * Get the motor voltage for a specified speed and torque
     */
    fun voltageForTorque(speed: Double, torque: Double): Double {
        // find out the sign of the friction voltage that needs to be applied
        val frictionVoltage = when {
            speed > kEpsilon -> this.frictionVoltage
            speed < -kEpsilon -> -this.frictionVoltage
            torque > kEpsilon -> this.frictionVoltage
            torque < -kEpsilon -> -this.frictionVoltage
            else -> return 0.0
        }
        // convert to volts and add the signed friction voltage
        return torque / torquePerVolt + speed / speedPerVolt + frictionVoltage
    }

    /**
     * Solves the kinematic state of the robot into the dynamic state
     */
    fun solve(kinematicState: KinematicState): DynamicState {

        val velocity = kinematicState.velocity
        val acceleration = kinematicState.acceleration

        // Determine the necessary torques on the left and right wheels to produce the desired wheel accelerations.
        val leftTorque = wheelRadius / 2.0 * (acceleration.linear * linearInertia -
                acceleration.angular * angularInertia / wheelbaseRadius -
                velocity.angular * angularDrag / wheelbaseRadius)

        val rightTorque = wheelRadius / 2.0 * (acceleration.linear * linearInertia +
                acceleration.angular * angularInertia / wheelbaseRadius +
                velocity.angular * angularDrag / wheelbaseRadius)

        // Solve for input voltages.
        val wheelVelocity = solve(velocity)
        val leftVoltage = voltageForTorque(wheelVelocity.left, leftTorque)
        val rightVoltage = voltageForTorque(wheelVelocity.right, rightTorque)

        return DynamicState(
                voltage = WheelState(left = leftVoltage, right = rightVoltage),
                torque = WheelState(left = leftTorque, right = rightTorque)
        )
    }

    companion object {
        const val kEpsilon = 1E-9
    }
}