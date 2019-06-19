package ca.warp7.frc.drive

import ca.warp7.frc.epsilonEquals
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.withSign

/**
 * Model of the kinematics, dynamics, and transmission of a differential drive robot
 *
 *  @param wheelRadius measured radius of wheels in m
 *
 *  @param wheelbaseRadius effective wheelbase radius in m
 *
 *  @param maxVelocity maximum loaded velocity in m/s
 *
 *  @param maxAcceleration maximum acceleration in m/s^2
 *
 *  @param maxFreeSpeed maximum free speed in m/s
 *
 *  @param speedPerVolt free speed of transmission per volt in (rad/s) / V
 *
 *  @param torquePerVolt stall torque of transmission per volt in (N * m) / V
 *
 *  @param frictionVoltage voltage that breaks static friction in V
 *
 *  @param linearInertia equivalent mass in kg
 *
 *  @param angularInertia equivalent moment of inertia kg * m^2
 *
 *  @param maxVoltage maximum voltage of transmission in V
 *
 *  @param angularDrag drag torque per speed in (N * m) / (rad/s)
 */
@Suppress("unused")
class DifferentialDriveModel(

        val wheelRadius: Double, // m
        val wheelbaseRadius: Double, // m

        val maxVelocity: Double, // m/s
        val maxAcceleration: Double, // m/s^2
        val maxFreeSpeed: Double, // m/s

        val speedPerVolt: Double, // (rad/s) / V
        val torquePerVolt: Double, // (N * m) / V
        val frictionVoltage: Double, // V

        val linearInertia: Double, // kg
        val angularInertia: Double, // kg * m^2

        val maxVoltage: Double, // V
        val angularDrag: Double // (N * m) / (rad/s)
) {

    companion object {
        const val kEpsilon = 1E-9
    }

    val frictionPercent: Double = frictionVoltage / maxVoltage

    /**
     * Solves the forward kinematics of the drive train by converting the
     * speeds/accelerations of wheels on each side into linear and angular
     * speed/acceleration
     *
     * Equations:
     * v = (left + right) / 2
     * w = (right - left) / L
     *
     * for velocity, solves (m/s, m/s) into (m/s, rad/s)
     * for acceleration, solves (m/s^2, m/s^2) into (m/s^2, rad/s^2)
     *
     * @param wheels the wheel state
     * @return the chassis state
     */
    fun solve(wheels: WheelState): ChassisState = ChassisState(
            linear = (wheels.left + wheels.right) / 2.0,
            angular = (wheels.right - wheels.left) / (2 * wheelbaseRadius)
    )


    /**
     * Solves the inverse kinematics of the drive train by converting linear
     * and angular speed/acceleration into the speeds/accelerations of wheels
     * on each side
     *
     * for velocity, solves (m/s, rad/s) into (m/s, m/s)
     * for acceleration, solves (m/s^2, rad/s^2) into (m/s^2, m/s^2)
     *
     * @param chassis the chassis state
     * @return the wheel state
     */
    fun solve(chassis: ChassisState): WheelState = WheelState(
            left = chassis.linear - chassis.angular * wheelbaseRadius,
            right = chassis.linear + chassis.angular * wheelbaseRadius
    )

    /**
     * Solves for the effective wheelbase radius given wheel velocity measured
     * by encoders and angular velocity measured by a gyro
     *
     * This is given by rearranging the above function
     * w = (right - left) / L
     * wL = right - left
     * L = (right - left) / w
     * L/2 = (right - left) / 2w
     *
     * @param wheels the velocity of each wheel in (m/s, m/s)
     * @param angular the angular velocity of the robot in rad/s
     * @return the effective wheelbase radius
     */
    fun solveWheelbase(wheels: WheelState, angular: Double): Double =
            (wheels.right - wheels.left) / (2 * angular)


    /**
     * Solves the maximum reachable linear and angular velocity based on the curvature.
     *
     * The equations are derived from `w(r + L / 2) = far side velocity`. Assume far side goes 100%,
     * we replace it with max velocity and isolate for angular velocity.
     *
     * Then we rearrange `w = (right - left)/L` into `left = maxV - wL`, substitute it into
     * `v = (left + right) / 2`, and get the equation for max linear velocity
     *
     * L is double of wheelBaseRadius, so calculations are simplified here.
     *
     * If curvature is 0, it just returns a ChassisState with no angular velocity.
     *
     * Future: Does this also work for acceleration???
     *
     * @param curvature the curvature of the path in m^-1
     * @param maxVel the maximum velocity of the faster wheel in m/s
     * @return the maximum reachable chassis velocity in (m/s, rad/s)
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
     * Get the free speed obtained at a specified voltage
     *
     * @param voltage desired voltage in V
     */
    fun freeSpeedAtVoltage(voltage: Double): Double = when {
        voltage > kEpsilon -> max(0.0, voltage - frictionVoltage) * speedPerVolt
        voltage < kEpsilon -> min(0.0, voltage + frictionVoltage) * speedPerVolt
        else -> 0.0
    }


    /**
     * Get the motor torque for a specified speed and voltage
     *
     * calculate the effective voltage (taking away the friction voltage),
     * then calculate torque based on the voltage left
     * Units: ((N * m) / V) * (V - (rad/s) / ((rad/s) / V) = N * m
     *
     * @param speed speed in rad/s
     * @param voltage voltage in V
     * @return torque in N * m
     */
    fun torqueForVoltage(speed: Double, voltage: Double): Double {

        var effectiveVoltage = voltage
        when {
            speed > kEpsilon -> effectiveVoltage -= frictionVoltage
            speed < -kEpsilon -> effectiveVoltage += frictionVoltage
            voltage > kEpsilon -> effectiveVoltage = max(0.0, voltage - frictionVoltage)
            voltage < -kEpsilon -> effectiveVoltage = min(0.0, voltage + frictionVoltage)
            else -> return 0.0
        }

        return torquePerVolt * (effectiveVoltage - speed / speedPerVolt)
    }


    /**
     * Get the motor voltage for a specified speed and torque
     *
     * find out the sign of the friction voltage that needs to be applied,
     * then convert to volts and add the signed friction voltage
     * Units: (N * m) / ((N * m) / V) + (rad/s) / ((rad/s) / V) + V = V
     *
     * @param chassisSpeed the speed of the chassis to determine friction voltage
     * @param wheelSpeed the desired speed in rad/s
     * @param wheelTorque the desired torque in N * m
     * @return voltage in V
     */
    fun voltageForTorque(chassisSpeed: Double, wheelSpeed: Double, wheelTorque: Double): Double {
        val frictionVoltage = when {
            chassisSpeed > kEpsilon -> frictionVoltage
            chassisSpeed < -kEpsilon -> -frictionVoltage
            wheelSpeed > kEpsilon -> frictionVoltage
            wheelSpeed < -kEpsilon -> -frictionVoltage
            wheelTorque > kEpsilon -> frictionVoltage
            wheelTorque < -kEpsilon -> -frictionVoltage
            else -> return 0.0
        }
        return wheelTorque / torquePerVolt + wheelSpeed / speedPerVolt + frictionVoltage
    }


    /**
     * Solves the kinematic state of the robot into the dynamic state
     * by calculating torque and then voltage
     *
     * Determines the necessary torques on the left and right wheels
     * to produce the desired wheel accelerations, which is a sum of
     * linear and angular acceleration forces, plus an angular drag
     * proportional to velocity.
     *
     * The result is multiplied by 0.5 because the forces needed by the
     * entire robot are distributed between the two wheels
     *
     * Then solve for input voltages based on velocity and torque
     *
     * Units for torque calculation:
     * m * (m/s^2 * kg - rad/s^2 * kg * m^2 / m - rad/s * ((N * m) / (rad/s)) / m)
     * = m * (m/s^2 * kg - 1/s^2 * kg * m^2 / m - 1/s * ((N * m) / (1/s)) / m)
     * = m * (N - N - N)
     * = N * m
     *
     * @param velocity required velocity in (m/s, rad/s)
     * @param acceleration required acceleration in (m/s^2, rad/s^2)
     * @return dynamic state in [(V, V), (N * m, N * m)]
     */
    fun solve(velocity: ChassisState, acceleration: ChassisState): DynamicState {

        val leftTorque = 0.5 * wheelRadius * (acceleration.linear * linearInertia -
                acceleration.angular * angularInertia / wheelbaseRadius -
                velocity.angular * angularDrag / wheelbaseRadius) // N * m

        val rightTorque = 0.5 * wheelRadius * (acceleration.linear * linearInertia +
                acceleration.angular * angularInertia / wheelbaseRadius +
                velocity.angular * angularDrag / wheelbaseRadius) // N * m

        val wheelVelocity = solve(velocity) // (m/s, m/s)
        val wheelVelocityRadians = wheelVelocity / wheelRadius // (rad/s, rad/s)

        val leftVoltage = voltageForTorque(velocity.linear, wheelVelocityRadians.left, leftTorque) // V
        val rightVoltage = voltageForTorque(velocity.linear, wheelVelocityRadians.right, rightTorque) // V

        return DynamicState(
                voltage = WheelState(left = leftVoltage, right = rightVoltage),
                velocity = wheelVelocity
        )
    }

}