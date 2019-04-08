package ca.warp7.frc.drive

import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
data class DifferentialDriveModel(
        val wheelRadius: Double,
        val wheelbaseRadius: Double,
        val maxVelocity: Double,
        val maxAcceleration: Double,
        val maxFreeSpeed: Double,
        val speedPerVolt: Double,
        val torquePerVolt: Double,
        val frictionVoltage: Double,
        val linearInertia: Double, // mass in kg
        val angularInertia: Double,
        val maxVolts: Double,
        val angularDrag: Double = 1.0
) {
    fun solve(wheels: WheelState) = ChassisState(
            linear = (wheels.left + wheels.right) / 2.0,
            angular = (wheels.right - wheels.left) / (2 * wheelbaseRadius)
    )

    fun solve(chassis: ChassisState) = WheelState(
            left = chassis.linear - chassis.angular * wheelbaseRadius,
            right = chassis.linear + chassis.angular * wheelbaseRadius
    )

    fun signedMaxAtCurvature(curvature: Double, maxVel: Double = maxVelocity) =
            signedMaxAtCurvature(curvature, maxVel, wheelbaseRadius)

    fun solvedMaxAtCurvature(curvature: Double) = solve(signedMaxAtCurvature(curvature))

    fun freeSpeedAtVoltage(voltage: Double): Double {
        return when {
            voltage > kEpsilon -> max(0.0, voltage - frictionVoltage) * speedPerVolt
            voltage < kEpsilon -> min(0.0, voltage + frictionVoltage) * speedPerVolt
            else -> 0.0
        }
    }

    fun torqueForVoltage(outputSpeed: Double, voltage: Double): Double {
        var effectiveVoltage = voltage
        when {
            outputSpeed > kEpsilon -> effectiveVoltage -= frictionVoltage
            outputSpeed < -kEpsilon -> effectiveVoltage += frictionVoltage
            voltage > kEpsilon -> effectiveVoltage = max(0.0, voltage - frictionVoltage)
            voltage < -kEpsilon -> effectiveVoltage = min(0.0, voltage + frictionVoltage)
            else -> return 0.0
        }
        return torquePerVolt * (effectiveVoltage - outputSpeed / speedPerVolt)
    }

    fun voltageForTorque(output_speed: Double, torque: Double): Double {
        val frictionVoltage = when {
            output_speed > kEpsilon -> this.frictionVoltage
            output_speed < -kEpsilon -> -this.frictionVoltage
            torque > kEpsilon -> this.frictionVoltage
            torque < -kEpsilon -> -this.frictionVoltage
            else -> return 0.0
        }
        return torque / torquePerVolt + output_speed / speedPerVolt + frictionVoltage
    }

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

        return DynamicState(WheelState(leftVoltage, rightVoltage), WheelState(leftTorque, rightTorque))
    }

    companion object {
        const val kEpsilon = 1E-12
    }
}